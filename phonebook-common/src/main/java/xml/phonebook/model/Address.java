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
 * <Address>
 *      <Type>VISITING_ADDRESS</Type>
 *      <Street>Customer Street 8 B 9</Street>
 *      <Street>(P.O. Box 190)</Street>
 *      <PostalCode>12346</PostalCode>
 *      <Town>Customerville</Town>
 * </Address>
 *
 * @author pashky
 */
@Root(name = "Address")
public class Address {
    @Element(name = "Type")
    private final AddressType type;

    @ElementList(inline = true, entry = "Street", required = false)
    private List<String> streetLines = Collections.emptyList();

    @Element(name = "PostalCode", required = false)
    private String postalCode;

    @Element(name = "Town", required = false)
    private String town;

    public Address(@Element(name = "Type") AddressType type) {
        if(type == null)
            throw new NullPointerException("Type can't be null");
        this.type = type;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public List<String> getStreetLines() {
        return Collections.unmodifiableList(streetLines);
    }

    public void addStreetLines(Collection<String> streetLines) {
        if(this.streetLines.isEmpty())
            this.streetLines = new ArrayList<String>(streetLines);
        else
            this.streetLines.addAll(streetLines);
    }

    public void setStreetLines(Collection<String> streetLines) {
        this.streetLines = new ArrayList<String>(streetLines);
    }

    public String getStreetAddress() {
        return StringUtils.join(streetLines, "\n");
    }


    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public AddressType getType() {
        return type;
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
