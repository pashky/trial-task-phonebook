package xml.phonebook.model;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.util.*;

/**
 * Base class for address/email/phone/etc types. Immutable, all instances are unique, so can be compared with ==
 *
 * @author pashky
 */
public abstract class AbstractType {
    /**
     * Instance creator interface
     * @param <Type> subclass of type
     */
    protected static interface Constructor<Type extends AbstractType> {
        /**
         * Create instance of type with name
         * @param name name of type
         * @return instance
         */
        Type create(String name);
    }

    /**
     * Registry of all available instances so far
     * @param <Type> subclass of type
     */
    protected static class Registry<Type extends AbstractType> implements Converter<Type> {
        private final Map<String, Type> registry = new HashMap<String, Type>();
        private final Constructor<Type> constructor;

        /**
         * Constructor
         * @param constructor need instance creator for concrete subclassed type
         */
        public Registry(Constructor<Type> constructor) {
            this.constructor = constructor;
        }

        /**
         * Add type to registry
         * @param type new type
         */
        public synchronized void register(Type type) {
            registry.put(type.getName(), type);
        }

        /**
         * Find type by name string
         * @param name name
         * @return found type or null
         */
        public synchronized Type findByName(String name) {
            return registry.get(name);
        }

        /**
         * Find type by name string or create new instance by means of constructor
         * @param name name
         * @return found or created type
         */
        public synchronized Type findOrCreateByName(String name) {
            Type result = findByName(name);
            if(result == null) {
                result = constructor.create(name);
            }
            return result;
        }

        /**
         * Gets list of all available instances
         * @return list of all types
         */
        public synchronized Collection<Type> all() {
            return Collections.unmodifiableCollection(registry.values());
        }

        /**
         * For XML serializer
         * @param inputNode
         * @return
         * @throws Exception
         */
        public Type read(InputNode inputNode) throws Exception {
            return findOrCreateByName(inputNode.getValue());
        }

        /**
         * For XML deserializer
         * @param outputNode
         * @param type
         * @throws Exception
         */
        public void write(OutputNode outputNode, Type type) throws Exception {
            outputNode.setValue(type.getName());
        }
    }

    private final String name;
    private final String description;

    /**
     * Not to be used directly. Only for pre-canned subtype instances.
     * @param name name
     * @param description human-readable description
     */
    protected AbstractType(String name, String description) {
        if(name == null || description == null)
            throw new NullPointerException("Name can't be null");

        this.name = name;
        this.description = description;
    }

    /**
     * @return name of type instance
     */
    public String getName() {
        return name;
    }

    /**
     * @return human readable description of type instance
     */
    public String getDescription() {
        return description == null ? getName() : description;
    }

    /**
     * Identity compare should be enough as those are unique
     * @param o other object
     * @return true if equal
     */
    public boolean equals(Object o) {
        return this == o;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "." + name;
    }
}
