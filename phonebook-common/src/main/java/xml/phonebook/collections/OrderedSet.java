package xml.phonebook.collections;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Set, which preserves order of addition, yet doesn't allow equal objects
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
     * Add or update existing element equal to new one. If there's alreay element equal() to new,
     * it gets replaced anyway, as equality may ignore some changed fields.
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

    /**
     * Replace existing element with new one, even if it's equal to current
     * @param old old element
     * @param t new element
     * @return true if replaced
     */
    public boolean replace(T old, T t) {
        int i = indexOf(old);
        if(i < 0) {
            return false;
        } else {
            set(i, t);
            return true;
        }
    }

    /**
     * Standard collection method, overriden in naive way to preserve this specific implementation
     * @param ts collection to add
     * @return true if anything was added
     */
    public boolean addAll(Collection<? extends T> ts) {
        boolean changed = false;
        for(T t : ts) {
            changed |= add(t);
        }
        return changed;
    }

    /**
     * Don't support this, as we keep order
     * @param i
     * @param t
     */
    public void add(int i, T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * Don't support this
     * @param i
     * @param ts
     * @return
     */
    public boolean addAll(int i, Collection<? extends T> ts) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns copy with some element replaced by new one. If not found, returns exact copy.
     * @param old element to replace
     * @param t replace with
     * @return copy of the set with change
     */
    public OrderedSet<T> withReplaced(T old, T t) {
        OrderedSet<T> result = new OrderedSet<T>(this);
        result.replace(old, t);
        return result;
    }

    /**
     * Returns copy with some element removed. If not found, returns exact copy.
     * @param t element to remove
     * @return copy of the set with change
     */
    public OrderedSet<T> withRemoved(T t) {
        OrderedSet<T> result = new OrderedSet<T>(this);
        result.remove(t);
        return result;
    }

    /**
     * Returns copy with some element added to the back if it's not yet there.
     * @param t element to add
     * @return copy of set with new element added
     */
    public OrderedSet<T> withAdded(T t) {
        OrderedSet<T> result = new OrderedSet<T>(this);
        result.add(t);
        return result;
    }
}
