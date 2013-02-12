package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Converter;

/**
 * Created 11/02/2013 23:20
 *
 * @author pashky
 */
@Element
public final class PhoneType extends AbstractType {
    private static final Registry<PhoneType> registry = new Registry<PhoneType>(new Constructor<PhoneType>() {
        public PhoneType create(String name) {
            return new PhoneType(name, name.toLowerCase().replaceFirst("_phone$", ""));
        }
    });

    private PhoneType(String name, String description) {
        super(name, description);
        registry.register(this);
    }

    public static PhoneType valueOf(String name) {
        return registry.findOrCreateByName(name);
    }

    public static Converter getConverter() {
        return registry;
    }

    public static final PhoneType WORK_PHONE = new PhoneType("WORK_PHONE", "work");
    public static final PhoneType HOME_PHONE = new PhoneType("HOME_PHONE", "home");
    public static final PhoneType MOBILE_PHONE = new PhoneType("MOBILE_PHONE", "mobile");
}
