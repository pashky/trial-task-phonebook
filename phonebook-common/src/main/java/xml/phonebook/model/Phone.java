package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * Phone class. Immutable.
 *
 * @author pashky
 */
@Root(name = "Phone")
public class Phone implements Searchable {
    @Element(name = "Type")
    private final PhoneType type;
    @Element(name = "Value")
    private final String phone;

    private Phone() {
        this.type = PhoneType.HOME_PHONE;
        this.phone = "";
    }

    /**
     * Normalize phone - i.e. remove all meaningless symbols like (,),-,etc.
     * This form is used for equality tests, so "+1 (234) 55-66-77" and "+1234556677" phones are considered equal.
     * @param phone phone
     * @return normalized form
     */
    private static String normalize(String phone) {
        return phone.toLowerCase().replaceAll("[^0-9pw*#+]", "");
    }

    /**
     * Constructor
     * @param type phone type
     * @param phone phone string
     */
    public Phone(@Element(name = "Type") PhoneType type, @Element(name = "Value") String phone) {
        if(phone == null || type == null)
            throw new NullPointerException("Phone and type can't be null");
        this.type = type;
        this.phone = phone;
    }

    /**
     *
     * @return phone string
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @return normalized phone string
     */
    public String getNormalizedPhone() {
        return normalize(phone);
    }

    /**
     *
     * @return phone type
     */
    public PhoneType getType() {
        return type;
    }

    /**
     * Returns a copy with new type
     * @param newType new type
     * @return copy of phone
     */
    public Phone withType(PhoneType newType) {
        return new Phone(newType, getPhone());
    }

    /**
     * Returns a copy with new phone
     * @param newPhone new phone
     * @return copy of phone
     */
    public Phone withPhone(String newPhone) {
        return new Phone(getType(), newPhone);
    }

    /**
     * Normalized form is used for equality tests, so "+1 (234) 55-66-77" and "+1234556677" phones are considered equal.
     * @param o other phone
     * @return true if type and phone are equal
     */
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

    public boolean matches(String text) {
        String normal = normalize(text);
        return normal.length() > 0 && getNormalizedPhone().contains(normal);
    }
}
