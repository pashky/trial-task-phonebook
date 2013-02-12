package xml.phonebook.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.*;

/**
 * Created 12/02/2013 01:43
 *
 * @author pashky
 */
public class AddressTest {
    @Test
    public void test_getStreetAddress() {
        Address a = new Address(AddressType.VISITING_ADDRESS, "", "", Arrays.asList("111", "222"));
        assertEquals("111\n222", a.getStreetAddress());
    }

    @Test
    public void testAddress() {
        Address a = new Address(AddressType.VISITING_ADDRESS, "New York", null, Arrays.asList("Some building", "123 High Street"));

        assertNotNull(a.getTown());
        assertNull(a.getPostalCode());
        assertNotNull(a.getStreetLines());
        assertEquals(2, a.getStreetLines().size());

        // check it's read-only
        try {
            Collection<String> lines = a.getStreetLines();
            lines.add("One more");
            fail();
        } catch (Exception e) {
            // should throw
        }
    }

    @Test
    public void testEquality() {
        Address a1 = new Address(AddressType.VISITING_ADDRESS, "123456", "New York", Arrays.asList("111", "222"));

        Address a2 = new Address(AddressType.VISITING_ADDRESS, "123456", "New York", Arrays.asList("111", "222"));
        assertEquals(a1, a2);

        Address a3 = a1.withType(AddressType.valueOf("SOME_OTHER_ADDRESS"));
        assertFalse(a1.equals(a3));

        Address a4 = a1.withTown("LA");
        assertFalse(a1.equals(a4));

        Address a5 = a1.withPostalCode("123456");
        assertFalse(a1.equals(a5));

        Address a6 = a1.withStreetLines(Arrays.asList("111", "222", "333"));
        assertFalse(a1.equals(a6));

        Address a7 = a1.withStreetLines(Arrays.asList("222", "111"));
        assertFalse(a1.equals(a7));
    }

}
