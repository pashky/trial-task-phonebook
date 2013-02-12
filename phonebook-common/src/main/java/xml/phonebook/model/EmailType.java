package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Converter;

/**
 * Created 11/02/2013 23:20
 *
 * @author pashky
 */
@Element
public final class EmailType extends AbstractType {
    private static final Registry<EmailType> registry = new Registry<EmailType>(new Constructor<EmailType>() {
        public EmailType create(String name) {
            return new EmailType(name, name.toLowerCase().replaceFirst("_email$", ""));
        }
    });

    private EmailType(String name, String description) {
        super(name, description);
        registry.register(this);
    }

    public static EmailType valueOf(String name) {
        return registry.findOrCreateByName(name);
    }

    public static Converter getConverter() {
        return registry;
    }

    public static final EmailType WORK_EMAIL = new EmailType("WORK_EMAIL", "work");
    public static final EmailType PERSONAL_EMAIL = new EmailType("PERSONAL_EMAIL", "personal");
}
