package xml.phonebook.model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created 12/02/2013 01:32
 *
 * @author pashky
 */
public class PhoneTest {
    
    @Test
    public void test_getNormalizedPhone() {
        Phone p = new Phone(PhoneType.HOME_PHONE, "+1 (234) 12-55-66 w *1234# HELLO");
        assertEquals("+1234125566w*1234#", p.getNormalizedPhone());
    }

    @Test
    public void testPhone() {
        Phone p1 = new Phone(PhoneType.WORK_PHONE, "+1 234 567892");
        assertEquals("+1 234 567892", p1.getPhone());
        assertEquals(PhoneType.WORK_PHONE, p1.getType());

        Phone p2 = new Phone(PhoneType.HOME_PHONE, p1.getPhone());
        assertFalse(p1.equals(p2));

        Phone p3 = new Phone(PhoneType.WORK_PHONE, "+1 234 567892");
        assertEquals(p1, p3);

        Phone p4 = new Phone(PhoneType.WORK_PHONE, "+2 768 1235");
        assertFalse(p1.equals(p4));

        Phone p5 = new Phone(PhoneType.WORK_PHONE, "+1 (234) 56-78-92");
        assertEquals(p1, p5);

    }
    
}
