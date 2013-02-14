package xml.phonebook.model;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Converter;

import java.util.Collection;

/**
 * Concrete subclass for phone types set
 *
 * @author pashky
 */
@Element
public final class PhoneType extends AbstractType {
    private static final Registry<PhoneType> registry = new Registry<PhoneType>(new Constructor<PhoneType>() {
        public PhoneType create(String name) {
            return new PhoneType(name, StringUtils.capitalize(name.toLowerCase().replaceFirst("_phone$", "")));
        }
    });

    /**
     * Not to be used directly. Only for pre-canned type instances.
     * @param name name
     * @param description human-readable description
     */
    private PhoneType(String name, String description) {
        super(name, description);
        registry.register(this);
    }

    /**
     * Return instance for type. May be created dynamically, if it's not found in pre-canned list
     * @param name name string
     * @return type instance
     */
    public static PhoneType valueOf(String name) {
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
    public static Collection<PhoneType> values() {
        return registry.all();
    }

    public static final PhoneType WORK_PHONE = new PhoneType("WORK_PHONE", "Work");
    public static final PhoneType HOME_PHONE = new PhoneType("HOME_PHONE", "Home");
    public static final PhoneType MOBILE_PHONE = new PhoneType("MOBILE_PHONE", "Mobile");
}
