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
public final class EmailType extends AbstractType {
    private static final Registry<EmailType> registry = new Registry<EmailType>(new Constructor<EmailType>() {
        public EmailType create(String name) {
            return new EmailType(name, StringUtils.capitalize(name.toLowerCase().replaceFirst("_email$", "")));
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

    public static Collection<EmailType> values() {
        return registry.all();
    }

    public static final EmailType WORK_EMAIL = new EmailType("WORK_EMAIL", "Work");
    public static final EmailType PERSONAL_EMAIL = new EmailType("PERSONAL_EMAIL", "Personal");
}
