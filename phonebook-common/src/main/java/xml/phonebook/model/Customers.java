package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created 12/02/2013 03:59
 *
 * @author pashky
 */
@Root(name = "Customers")
public class Customers {
    @ElementList(inline = true, required = false)
    private List<Customer> customerList = new ArrayList<Customer>() ;

    public Customers() {
    }

    public Customers(Collection<Customer> customerList) {
        this.customerList = new ArrayList<Customer>(customerList);
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public int numberOf() {
        return customerList.size();
    }

    public Customer getNth(int i) {
        return customerList.get(i);
    }
}
