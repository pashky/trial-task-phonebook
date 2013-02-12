package xml.phonebook.model;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created 11/02/2013 23:18
 *
 * Address class to represent this data structure:
 *
 * <Address>
 *      <Type>VISITING_ADDRESS</Type>
 *      <Street>Customer Street 8 B 9</Street>
 *      <Street>(P.O. Box 190)</Street>
 *      <PostalCode>12346</PostalCode>
 *      <Town>Customerville</Town>
 * </Address>
 *
 * Immutable.
 *
 * @author pashky
 */
@Root(name = "Address")
public final class Address {
    /**
     * Address type
     */
    @Element(name = "Type")
    private final AddressType type;

    /**
     * Array of street address lines
     */
    @ElementList(inline = true, entry = "Street", required = false)
    private List<String> streetLines;

    /**
     * Postal code
     */
    @Element(name = "PostalCode", required = false)
    private final String postalCode;

    @Element(name = "Town", required = false)
    private final String town;

    /**
     * This constructor only exists to serve XML serializer
     * @param type
     * @param town
     * @param postalCode
     */
    private Address(@Element(name = "Type") AddressType type,
                    @Element(name = "Town", required = false) String town,
                    @Element(name = "PostalCode", required = false) String postalCode
    ) {
        this.type = type;
        this.town = town;
        this.postalCode = postalCode;
        this.streetLines = Collections.emptyList();
    }

    public Address(AddressType type, String town, String postalCode, Collection<String> streetLines
    ) {
        if(type == null)
            throw new NullPointerException("Type can't be null");
        this.type = type;
        this.town = town;
        this.postalCode = postalCode;
        this.streetLines = streetLines == null ? Collections.<String>emptyList()
                : new ArrayList<String>(streetLines);
    }

    public String getPostalCode() {
        return postalCode;
    }

    public List<String> getStreetLines() {
        return Collections.unmodifiableList(streetLines);
    }

    public String getStreetAddress() {
        return StringUtils.join(streetLines, "\n");
    }

    public String getTown() {
        return town;
    }

    public AddressType getType() {
        return type;
    }

    public Address withType(AddressType newType) {
        return new Address(newType, getTown(), getPostalCode(), getStreetLines());
    }

    public Address withTown(String newTown) {
        return new Address(getType(), newTown, getPostalCode(), getStreetLines());
    }

    public Address withPostalCode(String newPostalCode) {
        return new Address(getType(), getTown(), newPostalCode, getStreetLines());
    }

    public Address withStreetLines(Collection<String> newStreetLines) {
        return new Address(getType(), getTown(), getPostalCode(), newStreetLines);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (postalCode != null ? !postalCode.equals(address.postalCode) : address.postalCode != null) return false;
        if (!StringUtils.join(streetLines, "\n").equals(StringUtils.join(address.streetLines, "\n"))) return false;
        if (town != null ? !town.equals(address.town) : address.town != null) return false;
        if (!type.equals(address.type)) return false;

        return true;
    }

    public int hashCode() {
        int result = type.hashCode();
        for(String street : streetLines) {
            result = 31 * result + street.hashCode();
        }
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (town != null ? town.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Address{" +
                "postalCode='" + postalCode + '\'' +
                ", type=" + type +
                ", streetLines=" + StringUtils.join(streetLines, "\\n") +
                ", town='" + town + '\'' +
                '}';
    }
}
