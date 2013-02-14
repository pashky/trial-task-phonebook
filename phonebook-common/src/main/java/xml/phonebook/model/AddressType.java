package xml.phonebook.model;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Converter;

import java.util.Collection;

/**
 * Concrete subclass for address types set
 *
 * @author pashky
 */
@Element
public final class AddressType extends AbstractType {
    /**
     * Registry instance is class-wide
     */
    private static final Registry<AddressType> registry = new Registry<AddressType>(new Constructor<AddressType>() {
        public AddressType create(String name) {
            return new AddressType(name, StringUtils.capitalize(name.toLowerCase().replaceFirst("_address$", "")));
        }
    });

    /**
     * Not to be used directly. Only for pre-canned type instances.
     * @param name name
     * @param description human-readable description
     */
    private AddressType(String name, String description) {
        super(name, description);
        registry.register(this);
    }

    /**
     * Return instance for type. May be created dynamically, if it's not found in pre-canned list
     * @param name name string
     * @return type instance
     */
    public static AddressType valueOf(String name) {
        return registry.findOrCreateByName(name);
    }

    /**
     * For XML serializer
     * @return converter
     */
    public static Converter getConverter() {
        return registry;
    }

    /**
     * Collection of all values available for type so far
     * @return collection of types
     */
    public static Collection<AddressType> values() {
        return registry.all();
    }

    public static final AddressType VISITING_ADDRESS = new AddressType("VISITING_ADDRESS", "Visiting");
    public static final AddressType CORPORATE_ADDRESS = new AddressType("CORPORATE_ADDRESS", "Corporate");
}
