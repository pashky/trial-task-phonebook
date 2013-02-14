package xml.phonebook.model;

/**
 * Interface for anything searchable
 *
 * @author pashky
 */
public interface Searchable {
    /**
     * Match text in the object
     * @param text text to search
     * @return true if found
     */
    boolean matches(String text);
}
