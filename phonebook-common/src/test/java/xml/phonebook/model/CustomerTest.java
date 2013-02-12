package xml.phonebook.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created 12/02/2013 03:53
 *
 * @author pashky
 */
public class CustomerTest {
    Address a = new Address(AddressType.VISITING_ADDRESS, "New York", "123456", Arrays.asList("123 Hight Street"));
    Phone p = new Phone(PhoneType.HOME_PHONE, "+1 2344 444444");
    Email e = new Email(EmailType.WORK_EMAIL, "a@aa.cc");
    
    Customer c = new CustomerBuilder("John Doe").toCustomer();

    @Test
    public void testReadOnly() {
        try {
            c.getAddresses().add(a);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }

        try {
            c.getPhones().add(p);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }

        try {
            c.getEmails().add(e);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
    }

    @Test
    public void test_addAddress() {
        Customer c2 = c.withAddress(a);
        assertEquals(0, c.getAddresses().size());
        assertEquals(1, c2.getAddresses().size());
    }

    @Test
    public void test_addPhone() {
        Customer c2 = c.withPhone(p);
        assertEquals(1, c2.getPhones().size());
        assertEquals(0, c.getPhones().size());
        Customer c3 = c2.withPhone(p.withPhone(p.getNormalizedPhone())); // should be same
        assertEquals(1, c3.getPhones().size());
    }

    @Test
    public void test_addEmail() {
        Customer c2 = c.withEmail(e);
        assertEquals(0, c.getEmails().size());
        assertEquals(1, c2.getEmails().size());
    }

    @Test
    public void test_updateAddresses() throws Exception {

    }

    @Test
    public void test_updatePhones() throws Exception {

    }

    @Test
    public void test_updateEmails() throws Exception {

    }
}
