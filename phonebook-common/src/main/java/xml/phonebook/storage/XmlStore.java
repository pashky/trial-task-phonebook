package xml.phonebook.storage;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.simpleframework.xml.Serializer;
import xml.phonebook.model.Customer;
import xml.phonebook.model.CustomerList;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * XML-backed customer database storage
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

    /**
     * Interface for XML write error listeners, as XML flushes are asynchronous
     */
    public interface WriteErrorListener {
        void xmlWriteError(Throwable e);
    }

    private WeakReference<WriteErrorListener> writeErrorListener = null;
    private final Object listenerLock = new Object();

    /**
     * Create store instance
     * @param xmlFile file to read and write to
     */
    public XmlStore(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    /**
     * Attempt to read xml file
     * @throws IOException when read or parse error occurs
     */
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

    private String addCustomerInternal(Customer customer) {
        int id = idGenerator++;
        customerIndex.put(String.valueOf(id), ImmutablePair.of(customer, id));
        return String.valueOf(id);
    }

    /**
     * Wait for any pending writes to finish and shut down storage. MUST be called before app stop.
     */
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
                // shouldn't happen
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
                    @SuppressWarnings("unchecked")
                    final Map<Customer,Integer> order = (Map<Customer,Integer>)new IdentityMap();
                    List<Customer> customers = new ArrayList<Customer>();
                    for(Pair<Customer,Integer> pair : customerIndex.values()) {
                        customers.add(pair.getLeft());
                        order.put(pair.getLeft(), pair.getRight());
                    }
                    Collections.sort(customers, new Comparator<Customer>() {
                        public int compare(Customer customer, Customer customer2) {
                            return order.get(customer).compareTo(order.get(customer2));
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
                                WriteErrorListener ref = null;
                                synchronized (listenerLock) {
                                    if(writeErrorListener != null) {
                                        ref = writeErrorListener.get();
                                    }
                                }
                                if(ref != null) {
                                    ref.xmlWriteError(e);
                                }
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

    /**
     * Find customer by id
     * @param id id to search
     * @return stored customer structure or null if not found
     */
    public StoredCustomer findCustomerById(String id) {
        synchronized (inMemoryLock) {
            Pair<Customer,Integer> pair = customerIndex.get(id);
            return pair != null ? new StoredCustomer(id, pair.getLeft()) : null;
        }
    }

    /**
     * Predicate to filter out customers
     */
    public interface Predicate {
        /**
         * Test for compliance
         * @param c customer
         * @return true if should match customer
         */
        boolean test(Customer c);
    }

    /**
     * Filters customers by applying predicate for each of them, only those returning true make it to result.
     * @param filter filter predicate
     * @return collection of matching stored customers, read-only
     */
    public Collection<StoredCustomer> findCustomers(Predicate filter) {
        final List<StoredCustomer> result = new ArrayList<StoredCustomer>();
        synchronized (inMemoryLock) {
            @SuppressWarnings("unchecked")
            final Map<Customer,Integer> order = (Map<Customer,Integer>)new IdentityMap();
            for(Map.Entry<String, Pair<Customer,Integer>> e : customerIndex.entrySet()) {
                if(filter == null || filter.test(e.getValue().getLeft())) {
                    order.put(e.getValue().getLeft(), e.getValue().getRight());
                    result.add(new StoredCustomer(e.getKey(), e.getValue().getLeft()));
                }
            }
            Collections.sort(result, new Comparator<StoredCustomer>() {
                public int compare(StoredCustomer a, StoredCustomer b) {
                    return order.get(a.getCustomer()).compareTo(order.get(b.getCustomer()));
                }
            });
        }
        return Collections.unmodifiableCollection(result);
    }

    /**
     * Find customers by searching them for text substring. Case-insensitive.
     * @param text text to search
     * @return found customers, read-only
     */
    public Collection<StoredCustomer> findCustomersByText(final String text) {
        return findCustomers(new Predicate() {
            public boolean test(Customer c) {
                return c.matches(text);
            }
        });
    }

    /**
     * Return all customers in DB
     * @return collection of customers, read-only
     */
    public Collection<StoredCustomer> findAllCustomers() {
        return findCustomers(null);
    }

    /**
     * Delete customer by id
     * @param id id
     * @return true if deleted
     */
    public boolean deleteCustomerById(String id) {
        boolean result;
        synchronized (inMemoryLock) {
             result = customerIndex.remove(id) != null;
        }
        flush();
        return result;
    }

    /**
     * Update customer with new data by id
     * @param id id to update
     * @param newCustomer new customer structure
     * @return true if found and updated
     */
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

    /**
     * Add new customer
     * @param customer customer to store
     * @return stored version of customer with assigned id
     */
    public StoredCustomer addCustomer(Customer customer) {
        String id;
        synchronized (inMemoryLock) {
            id = addCustomerInternal(customer);
        }
        flush();
        return new StoredCustomer(id, customer);
    }

    /**
     *
     * @return write error listener if exists or null
     */
    public WriteErrorListener getWriteErrorListener() {
        synchronized (listenerLock) {
            return writeErrorListener != null ? writeErrorListener.get() : null;
        }
    }

    /**
     * Sets new listener using weak reference
     * @param writeErrorListener new listener
     */
    public void setWriteErrorListener(WriteErrorListener writeErrorListener) {
        synchronized (listenerLock) {
            this.writeErrorListener = writeErrorListener != null ?
                    new WeakReference<WriteErrorListener>(writeErrorListener) : null;
        }
    }
}
