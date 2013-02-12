package xml.phonebook.storage;

import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import xml.phonebook.model.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

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
        Address a1 = new Address(AddressType.VISITING_ADDRESS, "New York", "123456", Arrays.asList("Corp Building", "123 High Street"));
        serializer.write(a1, sw);
        Address a2 = serializer.read(Address.class, sw.toString());
        assertEquals(a1, a2);
    }

    @Test
    public void testAddress2() throws Exception
    {
        StringWriter sw = new StringWriter();
        Address a1 = new Address(AddressType.VISITING_ADDRESS, null, null, null);
        serializer.write(a1, sw);
        Address a2 = serializer.read(Address.class, sw.toString());
        assertEquals(a1, a2);
    }

    @Test
    public void testCustomers() throws Exception
    {
        Address a1 = new Address(AddressType.VISITING_ADDRESS, "New York", "123456", null);

        Address a2 = new Address(AddressType.VISITING_ADDRESS, "Los Angeles", "455778",
                Arrays.asList("Corp Building", "123 High Street"));

        Customer c1 = new CustomerBuilder("John Smith")
                .notes("out best client")
                .email(new Email(EmailType.WORK_EMAIL, "work@smith.com"))
                .email(new Email(EmailType.PERSONAL_EMAIL, "smith.john@mail.com"))
                .address(a1)
                .toCustomer();

        Customer c2 = new CustomerBuilder("Alex Doe")
                .phone(new Phone(PhoneType.HOME_PHONE, "+1 234 555555"))
                .phone(new Phone(PhoneType.HOME_PHONE, "+1 234 789923"))
                .email(new Email(EmailType.PERSONAL_EMAIL, "alex.doe@mail.com"))
                .address(a2)
                .toCustomer();

        Customer c3 = new Customer("Homeless Man");

        Customers cs1 = new Customers(Arrays.asList(c1, c2, c3));

        StringWriter sw = new StringWriter();
        serializer.write(cs1, sw);
        Customers cs2 = serializer.read(Customers.class, sw.toString());
        assertEquals(cs2.numberOf(), 3);

        for(int i = 0; i < cs1.numberOf(); ++i) {
            assertEquals(cs1.getNth(i), cs2.getNth(i));
        }

    }

    @Test
    public void testInterlace() throws Exception {
        StringReader r = new StringReader("<Customer>\n" +
                "      <Name>Alex Doe</Name>\n" +
                "      <Address>\n" +
                "         <Type>VISITING_ADDRESS</Type>\n" +
                "         <Street>Corp Building</Street>\n" +
                "         <Street>123 High Street</Street>\n" +
                "         <PostalCode>455778</PostalCode>\n" +
                "         <Town>Los Angeles</Town>\n" +
                "      </Address>\n" +
                "      <Phone>\n" +
                "         <Type>HOME_PHONE</Type>\n" +
                "         <Value>+1 234 555555</Value>\n" +
                "      </Phone>\n" +
                "      <Email>\n" +
                "         <Type>PERSONAL_EMAIL</Type>\n" +
                "         <Value>alex.doe@mail.com</Value>\n" +
                "      </Email>\n" +
                "      <Phone>\n" +
                "         <Type>HOME_PHONE</Type>\n" +
                "         <Value>+1 234 789923</Value>\n" +
                "      </Phone>\n" +
                "      <Email>\n" +
                "         <Type>WORK_EMAIL</Type>\n" +
                "         <Value>work@doe.com</Value>\n" +
                "      </Email>\n" +
                "   </Customer>");

        Customer c = serializer.read(Customer.class, r);

        assertEquals(2, c.getEmails().size());
        assertEquals(2, c.getPhones().size());

        Iterator<Email> ie = c.getEmails().iterator();
        assertEquals(EmailType.PERSONAL_EMAIL, ie.next().getType());
        assertEquals(EmailType.WORK_EMAIL, ie.next().getType());

        Iterator<Phone> ip = c.getPhones().iterator();
        assertEquals("+1 234 555555", ip.next().getPhone());
        assertEquals("+1 234 789923", ip.next().getPhone());

    }


    @Test
    public void testDuplicate() throws Exception {
        StringReader r = new StringReader("<Customer>\n" +
                "      <Name>Alex Doe</Name>\n" +
                "      <Address>\n" +
                "         <Type>VISITING_ADDRESS</Type>\n" +
                "         <Street>Corp Building</Street>\n" +
                "         <Street>123 High Street</Street>\n" +
                "         <PostalCode>455778</PostalCode>\n" +
                "         <Town>Los Angeles</Town>\n" +
                "      </Address>\n" +
                "      <Address>\n" +
                "         <Type>VISITING_ADDRESS</Type>\n" +
                "         <Street>Corp Building</Street>\n" +
                "         <Street>123 High Street</Street>\n" +
                "         <PostalCode>455778</PostalCode>\n" +
                "         <Town>Los Angeles</Town>\n" +
                "      </Address>\n" +
                "      <Phone>\n" +
                "         <Type>HOME_PHONE</Type>\n" +
                "         <Value>+1 234 555555</Value>\n" +
                "      </Phone>\n" +
                "      <Email>\n" +
                "         <Type>PERSONAL_EMAIL</Type>\n" +
                "         <Value>alex.doe@mail.com</Value>\n" +
                "      </Email>\n" +
                "      <Phone>\n" +
                "         <Type>HOME_PHONE</Type>\n" +
                "         <Value>+1 (234) 555555</Value>\n" +
                "      </Phone>\n" +
                "      <Email>\n" +
                "         <Type>PERSONAL_EMAIL</Type>\n" +
                "         <Value>alex.doe@mail.com</Value>\n" +
                "      </Email>\n" +
                "   </Customer>");

        Customer c = serializer.read(Customer.class, r);

        assertEquals(1, c.getAddresses().size());
        assertEquals(1, c.getEmails().size());
        assertEquals(1, c.getPhones().size());

        Iterator<Email> ie = c.getEmails().iterator();
        assertEquals(EmailType.PERSONAL_EMAIL, ie.next().getType());

        Iterator<Phone> ip = c.getPhones().iterator();
        assertEquals("+1 (234) 555555", ip.next().getPhone()); // last one should be picked

    }
}
