package xml.phonebook.collections;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created 12/02/2013 13:22
 *
 * @author pashky
 */
public class OrderedSet<T> extends ArrayList<T> {

    public OrderedSet() {
    }

    public OrderedSet(Collection<? extends T> ts) {
        super(ts);
    }

    /**
     * Add or update existing element equal to new one
     * @param t new element
     * @return true if anything has changed
     */
    public boolean add(T t) {
        int i = indexOf(t);
        if(i < 0) {
            return super.add(t);
        } else {
            T old = get(i);
            if(old != t) {
                set(i, t);
                return true;
            }
            return false;
        }
    }

    public boolean replace(T old, T t) {
        int i = indexOf(old);
        if(i < 0) {
            return false;
        } else {
            set(i, t);
            return true;
        }
    }

    public boolean addAll(Collection<? extends T> ts) {
        boolean changed = false;
        for(T t : ts) {
            changed |= add(t);
        }
        return changed;
    }

    public void add(int i, T t) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int i, Collection<? extends T> ts) {
        throw new UnsupportedOperationException();
    }

    public OrderedSet<T> withReplaced(T old, T t) {
        OrderedSet<T> result = new OrderedSet<T>(this);
        result.replace(old, t);
        return result;
    }

    public OrderedSet<T> withRemoved(T t) {
        OrderedSet<T> result = new OrderedSet<T>(this);
        result.remove(t);
        return result;
    }

    public OrderedSet<T> withAdded(T t) {
        OrderedSet<T> result = new OrderedSet<T>(this);
        result.add(t);
        return result;
    }
}
