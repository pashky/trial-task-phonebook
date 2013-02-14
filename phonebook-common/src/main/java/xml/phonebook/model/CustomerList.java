package xml.phonebook.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents simple immutable customer list, used mainly for XML (de)serialization
 *
 * @author pashky
 */
@Root(name = "Customers")
public final class CustomerList implements Iterable<Customer> {
    @ElementList(inline = true, required = false)
    private List<Customer> customerList = new ArrayList<Customer>();

    public CustomerList() {
    }

    /**
     * Constructor
     * @param customerList list of customers
     */
    public CustomerList(Collection<Customer> customerList) {
        this.customerList = new ArrayList<Customer>(customerList);
    }

    /**
     * Get size
     * @return number of items
     */
    public int size() {
        return customerList.size();
    }

    /**
     * Get i-th element
     * @param i position
     * @return customer at position
     */
    public Customer get(int i) {
        return customerList.get(i);
    }

    /**
     * Support iteration
     * @return iterator for list
     */
    public Iterator<Customer> iterator() {
        return customerList.iterator();
    }
}
