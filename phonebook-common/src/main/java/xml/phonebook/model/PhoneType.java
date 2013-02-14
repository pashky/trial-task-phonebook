package xml.phonebook.model;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Converter;

import java.util.Collection;

/**
 * Created 11/02/2013 23:20
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

    public static Collection<PhoneType> values() {
        return registry.all();
    }

    public static final PhoneType WORK_PHONE = new PhoneType("WORK_PHONE", "Work");
    public static final PhoneType HOME_PHONE = new PhoneType("HOME_PHONE", "Home");
    public static final PhoneType MOBILE_PHONE = new PhoneType("MOBILE_PHONE", "Mobile");
}
