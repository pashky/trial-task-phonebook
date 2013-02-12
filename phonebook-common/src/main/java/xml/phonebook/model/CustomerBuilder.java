package xml.phonebook.model;

import xml.phonebook.collections.OrderedSet;

/**
 * Created 12/02/2013 14:42
 *
 * @author pashky
 */
public class CustomerBuilder {
    private String name;
    private String notes;

    private OrderedSet<Address> addresses = new OrderedSet<Address>();
    private OrderedSet<Phone> phones = new OrderedSet<Phone>();
    private OrderedSet<Email> emails = new OrderedSet<Email>();

    public CustomerBuilder(String name) {
        this.name = name;
    }

    public CustomerBuilder(Customer c) {
        name = c.getName();
        notes = c.getNotes();
        addresses.addAll(c.getAddresses());
        phones.addAll(c.getPhones());
        emails.addAll(c.getEmails());
    }

    public CustomerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder notes(String notes) {
        this.notes = notes;
        return this;
    }

    public CustomerBuilder address(Address address) {
        this.addresses.add(address);
        return this;
    }

    public CustomerBuilder phone(Phone phone) {
        this.phones.add(phone);
        return this;
    }

    public CustomerBuilder email(Email email) {
        this.emails.add(email);
        return this;
    }

    public Customer toCustomer() {
        return new Customer(name, notes, addresses, phones, emails);
    }

}
