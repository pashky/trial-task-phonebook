package xml.phonebook.storage;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import xml.phonebook.model.AddressType;
import xml.phonebook.model.EmailType;
import xml.phonebook.model.PhoneType;

/**
 * Created 12/02/2013 15:21
 *
 * @author pashky
 */
public class SerializerFactory {
    private static Registry registry = new Registry();
    static {
        try {
            registry.bind(PhoneType.class, PhoneType.getConverter());
            registry.bind(EmailType.class, EmailType.getConverter());
            registry.bind(AddressType.class, AddressType.getConverter());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Strategy strategy = new RegistryStrategy(registry);

    public static Serializer getSerializer() {
        return new Persister(strategy);
    }
}
