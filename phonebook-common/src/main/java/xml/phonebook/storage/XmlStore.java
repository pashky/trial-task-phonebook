package xml.phonebook.storage;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.simpleframework.xml.Serializer;
import xml.phonebook.model.Customer;
import xml.phonebook.model.CustomerList;

import java.io.File;
import java.io.IOException;
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
    private int idGenerator = 0;

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

    public void read() throws IOException {

        CustomerList list;
        synchronized (fileAccessLock) {
            Serializer serializer = SerializerFactory.getSerializer();
            try {
                list = serializer.read(CustomerList.class, xmlFile);
            } catch(IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        synchronized (inMemoryLock) {
            customerIndex.clear();
            idGenerator = 0;
            for(Customer customer : list) {
                addCustomerInternal(customer);
            }
        }
    }

    private void addCustomerInternal(Customer customer) {
        int id = idGenerator++;
        customerIndex.put(String.valueOf(id), ImmutablePair.of(customer, id));
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
                    // TODO: sort right
//                    Collections.sort(customers, new Comparator<Customer>() {
//                        public int compare(Customer customer, Customer customer2) {
//                            return customerIndex.get(customer.getName()).getRight()
//                                    .compareTo(customerIndex.get(customer2.getName()).getRight());
//                        }
//                    });
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

    public Customer findCustomerById(String id) {
        synchronized (inMemoryLock) {
            Pair<Customer,Integer> pair = customerIndex.get(id);
            return pair != null ? pair.getLeft() : null;
        }
    }

    public Collection<StoredCustomer> findCustomersByText(String text) {
        final List<StoredCustomer> result = new ArrayList<StoredCustomer>();
        synchronized (inMemoryLock) {
            for(Map.Entry<String,Pair<Customer,Integer>> e : customerIndex.entrySet()) {
                Customer c = e.getValue().getLeft();
                if(c.matches(text)) {
                    result.add(new StoredCustomer(e.getKey(), c));
                }
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    public Collection<StoredCustomer> findAllCustomers() {
        final List<StoredCustomer> result = new ArrayList<StoredCustomer>();
        synchronized (inMemoryLock) {
            for(Map.Entry<String, Pair<Customer,Integer>> e : customerIndex.entrySet()) {
                result.add(new StoredCustomer(e.getKey(), e.getValue().getLeft()));
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    public boolean deleteCustomerById(String id) {
        boolean result;
        synchronized (inMemoryLock) {
             result = customerIndex.remove(id) != null;
        }
        flush();
        return result;
    }

    public boolean updateCustomerById(String id, Customer newCustomer) {
        synchronized (inMemoryLock) {
            Pair<Customer,Integer> existing = customerIndex.get(id);
            if(existing == null) {
                return false;
            }

            customerIndex.put(id, ImmutablePair.of(newCustomer, existing.getRight()));
        }
        flush();
        return true;
    }

    public StoredCustomer addCustomer(Customer customer) {
        synchronized (inMemoryLock) {
            addCustomerInternal(customer);
        }
        flush();
        return new StoredCustomer(customer.getName(), customer);
    }
}
