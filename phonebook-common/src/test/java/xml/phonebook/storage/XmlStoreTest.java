package xml.phonebook.storage;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import xml.phonebook.model.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Created 12/02/2013 02:43
 *
 * @author pashky
 */
public class XmlStoreTest {
    File tempXml;

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


    @Before
    public void setup() throws Exception {
        tempXml = File.createTempFile("phonebook", "-temp.xml");

        FileUtils.writeStringToFile(tempXml,
                "<?xml version='1.0' encoding='utf-8'?>" +
                        "<Customers><Customer xmlns=\"http://www.arcusys.fi/customer-example\">\n" +
                        " <Name>Conan C. Customer</Name>\n" +
                        "<Address>\n" +
                        "      <Type>VISITING_ADDRESS</Type>\n" +
                        "      <Street>åäøü Customer Street 8 B 9</Street>\n" +
                        "      <Street>(P.O. Box 190)</Street>\n" +
                        "      <PostalCode>12346</PostalCode>\n" +
                        "      <Town>Customerville</Town>\n" +
                        "</Address>\n" +
                        "<Phone>\n" +
                        "      <Type>WORK_PHONE</Type>\n" +
                        "      <Value>+358 555 555 555</Value>\n" +
                        "</Phone>\n" +
                        "<Email>\n" +
                        "        <Type>WORK_EMAIL</Type>\n" +
                        " <Value>conan.c.customer@example.com</Value>\n" +
                        "</Email>\n" +
                        "<Phone>\n" +
                        " <Type>MOBILE_PHONE</Type>\n" +
                        "     <Value>+358 50 999 999 999</Value>\n" +
                        "</Phone>\n" +
                        " <Notes>                       Conan is a customer.               </Notes>\n" +
                        "</Customer><Customer><Name>Another Customer</Name></Customer></Customers>\n", Charset.forName("utf-8"));
    }

    @After
    public void cleanup() throws Exception {
        tempXml.delete();
    }

    @Test
    public void testRead() throws Exception {
        XmlStore store = new XmlStore(tempXml);
        store.read();

        StoredCustomer c = store.findCustomerById("0");
        assertNotNull(c);
        assertEquals("Conan C. Customer", c.getName());
        assertEquals(1, c.getEmails().size());
        assertEquals(2, c.getPhones().size());
        assertEquals(1, c.getAddresses().size());

        assertTrue(c.getAddresses().iterator().next().getStreetAddress().contains("åäøü"));
    }

    @Test
    public void testWrite() throws Exception {
        XmlStore store = new XmlStore(tempXml);
        store.read();
        store.addCustomer(c1);
        store.shutdown();

        String result = FileUtils.readFileToString(tempXml);
        assertTrue(result.startsWith("<?xml version='1.0' encoding='utf-8'?>"));
        assertTrue(result.contains("<Name>Conan C. Customer</Name>"));
        assertTrue(result.contains("<Name>John Smith</Name>"));
    }

    @Test
    public void testConcurrentWrite() throws Exception {
        final XmlStore store = new XmlStore(tempXml);
        store.read();

        ExecutorService service = Executors.newFixedThreadPool(3);

        Future<Void> f1 = service.submit(new Callable<Void>() {
            public Void call() throws Exception {
                store.addCustomer(c1);
                return null;
            }
        });

        Future<Void> f2 = service.submit(new Callable<Void>() {
            public Void call() throws Exception {
                store.addCustomer(c2);
                return null;
            }
        });

        Future<Void> f3 = service.submit(new Callable<Void>() {
            public Void call() throws Exception {
                store.deleteCustomerById("0");
                return null;
            }
        });

        f1.get();
        f2.get();
        f3.get();

        store.shutdown();
        String result = FileUtils.readFileToString(tempXml);

        assertTrue(result.startsWith("<?xml version='1.0' encoding='utf-8'?>"));
        assertFalse(result.contains("<Name>Conan C. Customer</Name>"));
        assertTrue(result.contains("<Name>John Smith</Name>"));
        assertTrue(result.contains("<Name>Alex Doe</Name>"));

    }

    @Test
    public void testUpdate() throws Exception {
        XmlStore store = new XmlStore(tempXml);
        store.read();
        StoredCustomer c = store.findCustomerById("0");
        store.updateCustomerById(c.getId(), c.getCustomer().withName("Renamed"));
        store.shutdown();

        String result = FileUtils.readFileToString(tempXml);
        assertTrue(result.startsWith("<?xml version='1.0' encoding='utf-8'?>"));
        assertTrue(result.contains("<Name>Renamed</Name>"));
    }

    @Test
    public void testDelete() throws Exception {
        XmlStore store = new XmlStore(tempXml);
        store.read();
        StoredCustomer c = store.findCustomerById("0");
        assertTrue(store.deleteCustomerById(c.getId()));
        assertFalse(store.deleteCustomerById("100500"));
        store.shutdown();

        String result = FileUtils.readFileToString(tempXml);
        assertTrue(result.startsWith("<?xml version='1.0' encoding='utf-8'?>"));
        assertFalse(result.contains("<Name>Conan C. Customer</Name>"));
    }

    @Test
    public void testSearch() throws Exception {
        XmlStore store = new XmlStore(tempXml);
        store.read();
        Collection<StoredCustomer> cs = store.findCustomersByText("conan");
        assertEquals(1, cs.size());

        cs = store.findCustomersByText("another");
        assertEquals(1, cs.size());

        cs = store.findCustomersByText("customer");
        assertEquals(2, cs.size());

        cs = store.findCustomersByText("zzzzzzz");
        assertEquals(0, cs.size());

        store.shutdown();
    }

    @Test
    public void testAll() throws Exception {
        XmlStore store = new XmlStore(tempXml);
        store.read();
        Collection<StoredCustomer> cs = store.findAllCustomers();
        assertEquals(2, cs.size());
        store.addCustomer(new Customer("Vasya", "one more"));
        cs = store.findAllCustomers();
        assertEquals(3, cs.size());
        store.shutdown();
    }

}
