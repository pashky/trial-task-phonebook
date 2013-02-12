package xml.phonebook.model;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.*;

/**
 * Created 11/02/2013 23:13
 *
 * @author pashky
 */
@Root(name = "Customer")
public class Customer {
    @Element(name = "Name")
    private String name;

    @Element(name = "Notes", required = false)
    private String notes;

    public Customer(@Element(name = "Name") String name) {
        if(name == null)
            throw new NullPointerException("Name can't be null");
        this.name = name;
    }

    @ElementList(inline = true, required = false)
    private List<Address> addresses = new ArrayList<Address>();

    @ElementList(inline = true, required = false)
    private List<Email> emails = new ArrayList<Email>();

    @ElementList(inline = true, required = false)
    private List<Phone> phones = new ArrayList<Phone>();

    public Collection<Address> getAddresses() {
        return Collections.unmodifiableCollection(addresses);
    }

    public Collection<Email> getEmails() {
        return Collections.unmodifiableCollection(emails);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Collection<Phone> getPhones() {
        return Collections.unmodifiableCollection(phones);
    }

    public static <T> void retain(List<T> list, T object) {
        int i = list.indexOf(object);
        if(i < 0) {
            list.add(object);
        } else {
            list.set(i, object);
        }
    }

    public static <T> void update(List<T> list, Collection<T> newList) {
        Set<T> toRemove = new HashSet<T>(list);
        for(T item : newList) {
            toRemove.remove(item);
            retain(list, item);
        }

        for(T item : toRemove) {
            list.remove(item);
        }
    }

    public void retainAddress(Address address) {
        retain(addresses, address);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
    }

    public void updateAddresses(Collection<Address> newAddresses) {
        update(addresses, newAddresses);
    }

    public void retainPhone(Phone phone) {
        retain(phones, phone);
    }

    public void removePhone(Phone phone) {
        phones.remove(phone);
    }

    public void updatePhones(Collection<Phone> newPhones) {
        update(phones, newPhones);
    }

    public void retainEmail(Email email) {
        retain(emails, email);
    }

    public void removeEmail(Email email) {
        emails.remove(email);
    }

    public void updateEmails(Collection<Email> newEmails) {
        update(emails, newEmails);
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
}
