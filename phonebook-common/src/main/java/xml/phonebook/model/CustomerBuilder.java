package xml.phonebook.model;

import xml.phonebook.collections.OrderedSet;

/**
 * Mutable utility class to build customer structures in the code.
 * All methods are chainable
 *
 * @author pashky
 */
public class CustomerBuilder {
    private String name;
    private String notes;

    private OrderedSet<Address> addresses = new OrderedSet<Address>();
    private OrderedSet<Phone> phones = new OrderedSet<Phone>();
    private OrderedSet<Email> emails = new OrderedSet<Email>();

    /**
     * Init customer with name
     * @param name name
     */
    public CustomerBuilder(String name) {
        this.name = name;
    }

    /**
     * Init with existing customer structure
     * @param c customer
     */
    public CustomerBuilder(Customer c) {
        name = c.getName();
        notes = c.getNotes();
        addresses.addAll(c.getAddresses());
        phones.addAll(c.getPhones());
        emails.addAll(c.getEmails());
    }

    /**
     * Set name
     * @param name name
     * @return self
     */
    public CustomerBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set notes
     * @param notes notes
     * @return self
     */
    public CustomerBuilder notes(String notes) {
        this.notes = notes;
        return this;
    }

    /**
     * Add address
     * @param address new address
     * @return self
     */
    public CustomerBuilder address(Address address) {
        this.addresses.add(address);
        return this;
    }

    /**
     * Add phone
     * @param phone new phone
     * @return self
     */
    public CustomerBuilder phone(Phone phone) {
        this.phones.add(phone);
        return this;
    }

    /**
     * Add email
     * @param email new email
     * @return self
     */
    public CustomerBuilder email(Email email) {
        this.emails.add(email);
        return this;
    }

    /**
     * Create immutable customer object
     * @return customer instance
     */
    public Customer toCustomer() {
        return new Customer(name, notes, addresses, phones, emails);
    }

}
