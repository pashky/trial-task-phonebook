package xml.phonebook.model;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.util.*;

/**
 * Created 11/02/2013 23:49
 *
 * @author pashky
 */
public abstract class AbstractType {
    protected static interface Constructor<Type extends AbstractType> {
        Type create(String name);
    }

    protected static class Registry<Type extends AbstractType> implements Converter<Type> {
        private final Map<String, Type> registry = new HashMap<String, Type>();
        private final Constructor<Type> constructor;

        public Registry(Constructor<Type> constructor) {
            this.constructor = constructor;
        }

        public synchronized void register(Type type) {
            registry.put(type.getName(), type);
        }

        public synchronized Type findByName(String name) {
            return registry.get(name);
        }

        public synchronized Type findOrCreateByName(String name) {
            Type result = findByName(name);
            if(result == null) {
                result = constructor.create(name);
            }
            return result;
        }

        public synchronized Collection<Type> all() {
            return Collections.unmodifiableCollection(registry.values());
        }

        public Type read(InputNode inputNode) throws Exception {
            return findOrCreateByName(inputNode.getValue());
        }

        public void write(OutputNode outputNode, Type type) throws Exception {
            outputNode.setValue(type.getName());
        }
    }

    private final String name;
    private final String description;

    protected AbstractType(String name, String description) {
        if(name == null || description == null)
            throw new NullPointerException("Name can't be null");

        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

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
