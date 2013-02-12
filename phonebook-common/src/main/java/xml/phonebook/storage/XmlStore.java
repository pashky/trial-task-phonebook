package xml.phonebook.storage;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.simpleframework.xml.Serializer;
import xml.phonebook.model.Customer;
import xml.phonebook.model.CustomerList;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created 12/02/2013 02:23
 *
 * @author pashky
 */
public class XmlStore {

    private final File xmlFile;
    private Map<String,Pair<Customer,Integer>> customerIndex = new HashMap<String, Pair<Customer, Integer>>();
    private int id = 0;

    private boolean isShutDown = false;

    private final Object fileAccessLock = new Object();
    private final Object inMemoryLock = new Object();
    private final Object flushTaskLock = new Object();

    private Future<Void> flushTask = null;
    private boolean pendingFlushes = false;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public XmlStore(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public void read() throws Exception {

        CustomerList list;
        synchronized (fileAccessLock) {
            Serializer serializer = SerializerFactory.getSerializer();
            try {
                list = serializer.read(CustomerList.class, xmlFile);
            } catch(Exception e) {
                throw new RuntimeException(e); // TODO what now?
            }
        }

        synchronized (inMemoryLock) {
            customerIndex.clear();
            id = 0;
            for(Customer customer : list) {
                customerIndex.put(customer.getName(), ImmutablePair.of(customer, id++));
            }
        }
    }

    public void shutdown() {
        while(true) {
            try {
                final Future<Void> toWait;
                synchronized (flushTaskLock) {
                    if(flushTask == null) {
                        isShutDown = true;
                        return;
                    }
                    toWait = flushTask;
                }
                toWait.get();
            } catch (InterruptedException e) {
                // just go on
            } catch (ExecutionException e) {
                // TODO log error
            }
        }
    }

    private void flush() {
        synchronized (flushTaskLock) {
            if(isShutDown) {
                throw new IllegalStateException("Store has been shut down already, no writes are possible");
            }
            if(flushTask == null) {
                final CustomerList list;
                synchronized (inMemoryLock) {
                    List<Customer> customers = new ArrayList<Customer>();
                    for(Pair<Customer,Integer> pair : customerIndex.values()) {
                        customers.add(pair.getLeft());
                    }
                    Collections.sort(customers, new Comparator<Customer>() {
                        public int compare(Customer customer, Customer customer2) {
                            return customerIndex.get(customer.getName()).getRight()
                                    .compareTo(customerIndex.get(customer2.getName()).getRight());
                        }
                    });
                    list = new CustomerList(customers);
                }
                flushTask = executorService.submit(new Runnable() {
                    public void run() {
                        synchronized (fileAccessLock) {
                            Serializer serializer = SerializerFactory.getSerializer();
                            try {
                                serializer.write(list, xmlFile);
                            } catch (Exception e) {
                                // do something TODO
                            }
                        }
                        synchronized (flushTaskLock) {
                            flushTask = null;
                            if(pendingFlushes) {
                                pendingFlushes = false;
                                flush();
                            }
                        }
                    }
                }, null);
            } else {
                pendingFlushes = true;
            }
        }
    }

    public Customer findCustomerByName(String name) {
        synchronized (inMemoryLock) {
            Pair<Customer,Integer> pair = customerIndex.get(name);
            return pair != null ? pair.getLeft() : null;
        }
    }

    public Collection<Customer> findCustomersByText(String text) {
        final List<Customer> result = new ArrayList<Customer>();
        synchronized (inMemoryLock) {
            for(Pair<Customer,Integer> pair : customerIndex.values()) {
                Customer c = pair.getLeft();
                if(c.matches(text)) {
                    result.add(c);
                }
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    public Collection<Customer> findAllCustomers() {
        final List<Customer> result = new ArrayList<Customer>();
        synchronized (inMemoryLock) {
            for(Pair<Customer,Integer> pair : customerIndex.values()) {
                Customer c = pair.getLeft();
                result.add(c);
            }
        }
        return Collections.unmodifiableCollection(result);
    }


    public boolean removeCustomerByName(String name) {
        boolean result;
        synchronized (inMemoryLock) {
             result = customerIndex.remove(name) != null;
        }
        flush();
        return result;
    }

    public boolean removeCustomer(Customer customer) {
        return removeCustomerByName(customer.getName());
    }

    public boolean updateCustomerByName(String name, Customer newCustomer) {
        synchronized (inMemoryLock) {
            Pair<Customer,Integer> existing = customerIndex.get(name);
            if(existing == null) {
                return false;
            }

            customerIndex.put(name, ImmutablePair.of(newCustomer, existing.getRight()));
        }
        flush();
        return true;
    }

    public boolean updateCustomer(Customer customer, Customer newCustomer) {
        return updateCustomerByName(customer.getName(), newCustomer);
    }

    public boolean addCustomer(Customer customer) {
        synchronized (inMemoryLock) {
            if(customerIndex.containsKey(customer.getName())) {
                return false;
            }
            customerIndex.put(customer.getName(), ImmutablePair.of(customer, id++));
        }
        flush();
        return true;
    }
}
