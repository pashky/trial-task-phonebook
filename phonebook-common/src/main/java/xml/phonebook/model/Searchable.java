package xml.phonebook.model;

/**
 * Created 12/02/2013 15:34
 *
 * @author pashky
 */
public interface Searchable {
    boolean matches(String text);
}
