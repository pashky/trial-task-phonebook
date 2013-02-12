package xml.phonebook.model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

/**
 * Created 12/02/2013 00:42
 *
 * @author pashky
 */
public class EverythingTypeTest {
    @Test
    public void testTypeValueOf() {
        // test for email type, but actually test abstract type, others should just work

        // pre-canned
        assertEquals(EmailType.WORK_EMAIL, EmailType.valueOf("WORK_EMAIL"));
        assertEquals(EmailType.PERSONAL_EMAIL, EmailType.valueOf("PERSONAL_EMAIL"));

        // missing
        EmailType other = EmailType.valueOf("SOME_OTHER_EMAIL");
        assertEquals("SOME_OTHER_EMAIL", other.getName());
        assertNotNull(other.getDescription()); // check it has some description

        // check if same name returns same instance
        EmailType other2 = EmailType.valueOf("SOME_OTHER_EMAIL");
        assertEquals(other, other2);

        // check others work too
        assertEquals(PhoneType.HOME_PHONE, PhoneType.valueOf("HOME_PHONE"));
        assertEquals(PhoneType.MOBILE_PHONE, PhoneType.valueOf("MOBILE_PHONE"));
        assertEquals(PhoneType.WORK_PHONE, PhoneType.valueOf("WORK_PHONE"));

        assertEquals(AddressType.VISITING_ADDRESS, AddressType.valueOf("VISITING_ADDRESS"));
    }

    @Test
    public void testCrossTypeInterference() {
        // check registries are separate
        EmailType emailType = EmailType.valueOf("TEST");
        PhoneType phoneType = PhoneType.valueOf("TEST");
        AddressType addressType = AddressType.valueOf("TEST");
        assertNotSame(emailType, phoneType);
        assertNotSame(phoneType, addressType);
        assertNotSame(addressType, emailType);
    }
}
