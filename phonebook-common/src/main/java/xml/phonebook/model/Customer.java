package xml.phonebook.model;

import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import xml.phonebook.collections.OrderedSet;

import java.util.*;

/**
 * Customer class. Immutable. All changes are only through copy-and-modify methods.
 *
 * @author pashky
 */
@Root(name = "Customer")
public final class Customer implements Searchable {
    @Element(name = "Name")
    private final String name;

    @Element(name = "Notes", required = false)
    private final String notes;

    public Customer(String name) {
        this(name, null);
    }

    public Customer(@Element(name = "Name") String name,
                    @Element(name = "Notes") String notes) {
        if(name == null)
            throw new NullPointerException("Name can't be null");
        this.name = name;
        this.notes = notes;
    }

    Customer(String name, String notes,
                     OrderedSet<Address> addresses, OrderedSet<Phone> phones, OrderedSet<Email> emails) {
        if(name == null || addresses == null || phones == null || emails == null)
            throw new NullPointerException("Can't be null");

        this.name = name;
        this.notes = notes;
        this.addresses = addresses;
        this.phones = phones;
        this.emails = emails;
    }

    @ElementList(inline = true, required = false, entry = "Address", type = Address.class)
    private OrderedSet<Address> addresses = new OrderedSet<Address>();

    @ElementList(inline = true, required = false, entry = "Email", type = Email.class)
    private OrderedSet<Email> emails = new OrderedSet<Email>();

    @ElementList(inline = true, required = false, entry = "Phone", type = Phone.class)
    private OrderedSet<Phone> phones = new OrderedSet<Phone>();

    public Collection<Address> getAddresses() {
        return Collections.unmodifiableCollection(addresses);
    }

    public Collection<Email> getEmails() {
        return Collections.unmodifiableCollection(emails);
    }

    public Collection<Phone> getPhones() {
        return Collections.unmodifiableCollection(phones);
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public Customer withName(String name) {
        return new Customer(name, notes, addresses, phones, emails);
    }

    public Customer withNotes(String notes) {
        return new Customer(name, notes, addresses, phones, emails);
    }

    public Customer withAddress(Address address) {
        return new Customer(name, notes, addresses.withAdded(address), phones, emails);
    }

    public Customer withoutAddress(Address address) {
        return new Customer(name, notes, addresses.withRemoved(address), phones, emails);
    }

    public Customer replaceAddress(Address old, Address address) {
        return new Customer(name, notes, addresses.withReplaced(old, address), phones, emails);
    }

    public Customer withPhone(Phone phone) {
        return new Customer(name, notes, addresses, phones.withAdded(phone), emails);
    }

    public Customer withoutPhone(Phone phone) {
        return new Customer(name, notes, addresses, phones.withRemoved(phone), emails);
    }

    public Customer replacePhone(Phone old, Phone phone) {
        return new Customer(name, notes, addresses, phones.withReplaced(old, phone), emails);
    }

    public Customer withEmail(Email email) {
        return new Customer(name, notes, addresses, phones, emails.withAdded(email));
    }

    public Customer withoutEmail(Email email) {
        return new Customer(name, notes, addresses, phones, emails.withRemoved(email));
    }

    public Customer replaceEmail(Email old, Email email) {
        return new Customer(name, notes, addresses, phones, emails.withReplaced(old, email));
    }

    public static <T> boolean setEquals(Collection<T> c1, Collection<T> c2) {
        return new HashSet<T>(c1).equals(new HashSet<T>(c2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (!setEquals(addresses, customer.addresses)) return false;
        if (!setEquals(emails, customer.emails)) return false;
        if (!setEquals(phones, customer.phones)) return false;

        if (!name.equals(customer.name)) return false;
        if (notes != null ? !notes.equals(customer.notes) : customer.notes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + addresses.hashCode();
        result = 31 * result + emails.hashCode();
        result = 31 * result + phones.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "addresses=[" + StringUtils.join(addresses, "; ") +
                "], name='" + name + '\'' +
                ", notes='" + notes + '\'' +
                ", emails=[" + StringUtils.join(emails, "; ") +
                "], phones=" + StringUtils.join(phones, "; ") +
                "]}";
    }

    public boolean matches(String text) {

        String ltext = text.toLowerCase();
        if((getName() != null && getName().toLowerCase().contains(ltext))
                || (getNotes() != null && getNotes().toLowerCase().contains(ltext))) {
            return true;
        }

        Iterator i = new IteratorChain(new Iterator[] {
                getAddresses().iterator(), getPhones().iterator(), getEmails().iterator()
        });
        while(i.hasNext()) {
            if(((Searchable)i.next()).matches(text))
                return true;
        }

        return false;
    }
}
