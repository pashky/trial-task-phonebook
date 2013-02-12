package xml.phonebook.parser;

import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import xml.phonebook.model.*;

import java.io.StringWriter;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * Created 12/02/2013 03:55
 *
 * @author pashky
 */
public class XmlSerializationTest {
    Registry registry;
    Strategy strategy;
    Serializer serializer;
    StringWriter sw;

    @Before
    public void setup() throws Exception {
        registry = new Registry();
        registry.bind(PhoneType.class, PhoneType.getConverter());
        registry.bind(EmailType.class, EmailType.getConverter());
        registry.bind(AddressType.class, AddressType.getConverter());
        strategy = new RegistryStrategy(registry);

        serializer = new Persister(strategy);
        sw = new StringWriter();
    }

    @Test
    public void testPhone() throws Exception
    {
        Phone p1 = new Phone(PhoneType.HOME_PHONE, "+1 234 567899");
        serializer.write(p1, sw);
        Phone p2 = serializer.read(Phone.class, sw.toString());
        assertEquals(p1, p2);
    }

    @Test
    public void testEmail() throws Exception
    {
        StringWriter sw = new StringWriter();
        Email e1 = new Email(EmailType.PERSONAL_EMAIL, "test.email@fmail.com");
        serializer.write(e1, sw);
        Email e2 = serializer.read(Email.class, sw.toString());
        assertEquals(e1, e2);
    }

    @Test
    public void testAddress1() throws Exception
    {
        StringWriter sw = new StringWriter();
        Address a1 = new Address(AddressType.VISITING_ADDRESS);
        a1.setTown("New York");
        a1.setPostalCode("123456");
        a1.setStreetLines(Arrays.asList("Corp Building", "123 High Street"));
        serializer.write(a1, sw);
        Address a2 = serializer.read(Address.class, sw.toString());
        assertEquals(a1, a2);
    }

    @Test
    public void testAddress2() throws Exception
    {
        StringWriter sw = new StringWriter();
        Address a1 = new Address(AddressType.VISITING_ADDRESS);
        serializer.write(a1, sw);
        Address a2 = serializer.read(Address.class, sw.toString());
        assertEquals(a1, a2);
    }

    @Test
    public void testCustomers() throws Exception
    {
        Address a1 = new Address(AddressType.VISITING_ADDRESS);
        a1.setTown("New York");
        a1.setPostalCode("123456");

        Address a2 = new Address(AddressType.VISITING_ADDRESS);
        a2.setTown("Los Angeles");
        a2.setPostalCode("455778");
        a2.setStreetLines(Arrays.asList("Corp Building", "123 High Street"));

        Customer c1 = new Customer("John Smith");
        c1.retainEmail(new Email(EmailType.WORK_EMAIL, "work@smith.com"));
        c1.retainEmail(new Email(EmailType.PERSONAL_EMAIL, "smith.john@mail.com"));
        c1.retainAddress(a1);

        Customer c2 = new Customer("Alex Doe");
        c2.retainPhone(new Phone(PhoneType.HOME_PHONE, "+1 234 555555"));
        c2.retainPhone(new Phone(PhoneType.HOME_PHONE, "+1 234 789923"));
        c2.retainEmail(new Email(EmailType.PERSONAL_EMAIL, "alex.doe@mail.com"));
        c2.retainAddress(a2);

        Customer c3 = new Customer("Alex Doe");

        Customers cs1 = new Customers(Arrays.asList(c1, c2, c3));

        StringWriter sw = new StringWriter();
        serializer.write(cs1, sw);
        Customers cs2 = serializer.read(Customers.class, sw.toString());
        assertEquals(cs2.numberOf(), 3);

        for(int i = 0; i < cs1.numberOf(); ++i) {
            assertEquals(cs1.getNth(i), cs2.getNth(i));
        }

    }

}
