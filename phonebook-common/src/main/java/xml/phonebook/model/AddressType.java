package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Converter;

/**
 * Created 12/02/2013 00:12
 *
 * @author pashky
 */
@Element
public final class AddressType extends AbstractType {
    private static final Registry<AddressType> registry = new Registry<AddressType>(new Constructor<AddressType>() {
        public AddressType create(String name) {
            return new AddressType(name, name.toLowerCase().replaceFirst("_address$", ""));
        }
    });

    private AddressType(String name, String description) {
        super(name, description);
        registry.register(this);
    }

    public static AddressType valueOf(String name) {
        return registry.findOrCreateByName(name);
    }

    public static Converter getConverter() {
        return registry;
    }

    public static final AddressType VISITING_ADDRESS = new AddressType("VISITING_ADDRESS", "visiting");
}
