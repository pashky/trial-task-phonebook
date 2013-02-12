package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * Created 11/02/2013 23:19
 *
 * @author pashky
 */
@Root(name = "Phone")
public class Phone {
    @Element(name = "Type")
    private final PhoneType type;
    @Element(name = "Value")
    private final String phone;

    private static String normalize(String phone) {
        return phone.toLowerCase().replaceAll("[^0-9pw*#+]", "");
    }

    public Phone(@Element(name = "Type") PhoneType type, @Element(name = "Value") String phone) {
        if(phone == null || type == null)
            throw new NullPointerException("Phone and type can't be null");
        this.type = type;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getNormalizedPhone() {
        return normalize(phone);
    }

    public PhoneType getType() {
        return type;
    }

    public Phone withType(PhoneType newType) {
        return new Phone(newType, getPhone());
    }

    public Phone withPhone(String newPhone) {
        return new Phone(getType(), newPhone);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Phone phone1 = (Phone) o;

        if (!getNormalizedPhone().equals(phone1.getNormalizedPhone())) return false;
        if (!type.equals(phone1.type)) return false;

        return true;
    }

    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + getNormalizedPhone().hashCode();
        return result;
    }

    public String toString() {
        return "Phone{" +
                "phone='" + phone + '\'' +
                ", type=" + type +
                '}';
    }
}
