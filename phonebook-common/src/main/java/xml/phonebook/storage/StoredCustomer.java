package xml.phonebook.storage;

import xml.phonebook.model.Address;
import xml.phonebook.model.Customer;
import xml.phonebook.model.Email;
import xml.phonebook.model.Phone;

import java.util.Collection;

/**
 * Facade for union of customer and its id in the storage. Immutable.
 *
 * @author pashky
 */
public final class StoredCustomer {
    private final String id;
    private final Customer customer;

    /**
     * Construct
     * @param id storage id
     * @param customer customer
     */
    public StoredCustomer(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    /**
     *
     * @return storage id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Delegate
     * @return addresses of the customer
     */
    public Collection<Address> getAddresses() {
        return customer.getAddresses();
    }

    /**
     * Delegate
     * @return emails of the customer
     */
    public Collection<Email> getEmails() {
        return customer.getEmails();
    }

    /**
     * Delegate
     * @return name of the customer
     */
    public String getName() {
        return customer.getName();
    }

    /**
     * Delegate
     * @return notes of the customer
     */
    public String getNotes() {
        return customer.getNotes();
    }

    /**
     * Delegate
     * @return phones of the customer
     */
    public Collection<Phone> getPhones() {
        return customer.getPhones();
    }

    /**
     * Delegate
     * @return true if text matches
     */
    public boolean matches(String text) {
        return customer.matches(text);
    }
}
