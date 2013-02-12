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
        Address a = new Address(AddressType.VISITING_ADDRESS);
        a.setStreetLines(Arrays.asList("111", "222"));
        assertEquals("111\n222", a.getStreetAddress());
    }

    @Test
    public void testAddress() {
        Address a = new Address(AddressType.VISITING_ADDRESS);
        a.setTown("New York");

        assertNotNull(a.getTown());
        assertNull(a.getPostalCode());

        a.addStreetLines(Arrays.asList("Some building", "123 High Street"));
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

        a.addStreetLines(Arrays.asList("One more"));
        assertEquals("Some building\n123 High Street\nOne more", a.getStreetAddress());
    }

    @Test
    public void testEquality() {
        Address a1 = new Address(AddressType.VISITING_ADDRESS);
        a1.setTown("New York");
        a1.setPostalCode("123456");
        a1.setStreetLines(Arrays.asList("111", "222"));

        Address a2 = new Address(AddressType.VISITING_ADDRESS);
        a2.setTown("New York");
        a2.setPostalCode("123456");
        a2.setStreetLines(Arrays.asList("111", "222"));

        assertEquals(a1, a2);

        Address a3 = new Address(AddressType.valueOf("OTHER_ADDRESS"));
        a3.setTown("New York");
        a3.setPostalCode("123456");

        assertFalse(a1.equals(a3));

        a2.setTown("LA");
        a2.setPostalCode("123456");
        assertFalse(a1.equals(a2));

        a2.setTown("New York");
        a2.setPostalCode("654321");
        assertFalse(a1.equals(a2));

        a2.setTown("New York");
        a2.setPostalCode("123456");
        a2.addStreetLines(Arrays.asList("333"));
        assertFalse(a1.equals(a2));
        a2.setStreetLines(Arrays.asList("222", "111"));
        assertFalse(a1.equals(a2));
    }

}
