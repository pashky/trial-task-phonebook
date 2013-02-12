package xml.phonebook.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created 11/02/2013 23:19
 *
 * @author pashky
 */
@Root(name = "Email")
public final class Email implements Searchable {
    @Element(name = "Type")
    private final EmailType type;
    @Element(name = "Value")
    private final String email;

    public Email(@Element(name = "Type") EmailType type, @Element(name = "Value") String email) {
        if(email == null || type == null)
            throw new NullPointerException("Email and type can't be null");
        this.type = type;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public EmailType getType() {
        return type;
    }

    public Email withType(EmailType newType) {
        return new Email(newType, getEmail());
    }

    public Email withEmail(String newEmail) {
        return new Email(getType(), newEmail);
    }

    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Email email1 = (Email) o;

        if (!email.equals(email1.email)) return false;
        if (!type.equals(email1.type)) return false;

        return true;
    }

    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }

    public String toString() {
        return "Email{" +
                "email='" + email + '\'' +
                ", type=" + type +
                '}';
    }

    public boolean matches(String text) {
        return getEmail().toLowerCase().contains(text.toLowerCase());
    }
}
