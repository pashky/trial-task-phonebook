package xml.phonebook.model;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

/**
 * Created 12/02/2013 01:17
 *
 * @author pashky
 */
public class EmailTest {
    @Test
    public void testEmail() {
        Email e1 = new Email(EmailType.WORK_EMAIL, "a@a.aa");
        assertEquals("a@a.aa", e1.getEmail());
        assertEquals(EmailType.WORK_EMAIL, e1.getType());

        Email e2 = new Email(EmailType.PERSONAL_EMAIL, e1.getEmail());
        assertFalse(e1.equals(e2));

        Email e3 = new Email(EmailType.WORK_EMAIL, "a@a.aa");
        assertEquals(e1, e3);

        Email e4 = new Email(EmailType.WORK_EMAIL, "b@bb.bb");
        assertFalse(e1.equals(e4));

        Email e5 = e1.withType(EmailType.WORK_EMAIL);
        assertEquals(e1.getEmail(), e5.getEmail());

        Email e6 = e1.withEmail("new@mail.com");
        assertEquals(e1.getType(), e6.getType());

        assertTrue(e1.matches("a@a"));
        assertFalse(e1.matches("jjj"));
    }
}
