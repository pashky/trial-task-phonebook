package xml.phonebook.storage;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.Format;
import xml.phonebook.model.AddressType;
import xml.phonebook.model.EmailType;
import xml.phonebook.model.PhoneType;

/**
 * Helper class to get XML serializer for all out classes
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

    private static Format format = new Format(2, "<?xml version='1.0' encoding='utf-8'?>");

    /**
     *
     * @return serializer instance, suitable for Customer (de)serialization
     */
    public static Serializer getSerializer() {
        return new Persister(strategy, format);
    }
}
