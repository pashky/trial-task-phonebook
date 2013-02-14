package xml.phonebook.model;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Converter;

import java.util.Collection;

/**
 * Concrete subclass for email types set
 *
 * @author pashky
 */
@Element
public final class EmailType extends AbstractType {
    /**
     * Registry instance is class-wide
     */
    private static final Registry<EmailType> registry = new Registry<EmailType>(new Constructor<EmailType>() {
        public EmailType create(String name) {
            return new EmailType(name, StringUtils.capitalize(name.toLowerCase().replaceFirst("_email$", "")));
        }
    });

    /**
     * Not to be used directly. Only for pre-canned type instances.
     * @param name name
     * @param description human-readable description
     */
    private EmailType(String name, String description) {
        super(name, description);
        registry.register(this);
    }

    /**
     * Return instance for type. May be created dynamically, if it's not found in pre-canned list
     * @param name name string
     * @return type instance
     */
    public static EmailType valueOf(String name) {
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
    public static Collection<EmailType> values() {
        return registry.all();
    }

    public static final EmailType WORK_EMAIL = new EmailType("WORK_EMAIL", "Work");
    public static final EmailType PERSONAL_EMAIL = new EmailType("PERSONAL_EMAIL", "Personal");
}
