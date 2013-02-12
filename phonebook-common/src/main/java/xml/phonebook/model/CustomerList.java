package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.*;

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

    public CustomerList(Collection<Customer> customerList) {
        this.customerList = new ArrayList<Customer>(customerList);
    }

    public int size() {
        return customerList.size();
    }

    public Customer get(int i) {
        return customerList.get(i);
    }

    public Iterator<Customer> iterator() {
        return customerList.iterator();
    }
}
