package xml.phonebook.storage;

import xml.phonebook.model.Address;
import xml.phonebook.model.Customer;
import xml.phonebook.model.Email;
import xml.phonebook.model.Phone;

import java.util.Collection;

/**
 * Created 14/02/2013 08:47
 *
 * @author pashky
 */
public class StoredCustomer {
    private final String id;
    private final Customer customer;

    public StoredCustomer(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Collection<Address> getAddresses() {
        return customer.getAddresses();
    }

    public Collection<Email> getEmails() {
        return customer.getEmails();
    }

    public String getName() {
        return customer.getName();
    }

    public String getNotes() {
        return customer.getNotes();
    }

    public Collection<Phone> getPhones() {
        return customer.getPhones();
    }

    public boolean matches(String text) {
        return customer.matches(text);
    }
}
