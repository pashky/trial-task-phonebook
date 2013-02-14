package xml.phonebook.server;

import org.junit.Test;
import xml.phonebook.model.*;
import xml.phonebook.server.JsonHelper;
import xml.phonebook.storage.StoredCustomer;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created 14/02/2013 05:51
 *
 * @author pashky
 */
public class JsonHelperTest {

    @Test
    public void testToJson()
    {
        Address a1 = new Address(AddressType.VISITING_ADDRESS, "Los Angeles", "455778",
                Arrays.asList("Corp Building", "123 High Street"));

        Customer c1 = new CustomerBuilder("John Smith")
                .notes("out best client")
                .email(new Email(EmailType.WORK_EMAIL, "work@smith.com"))
                .email(new Email(EmailType.PERSONAL_EMAIL, "smith.john@mail.com"))
                .phone(new Phone(PhoneType.MOBILE_PHONE, "+7 (234) 4444343"))
                .address(a1)
                .toCustomer();

        JsonHelper js = JsonHelper.instance();

        String json = "{\"name\":\"John Smith\",\"notes\":\"out best client\",\"addresses\":[{\"type\":\"VISITING_ADDRESS\",\"streetLines\":[\"Corp Building\",\"123 High Street\"],\"postalCode\":\"455778\",\"town\":\"Los Angeles\"}],\"emails\":[{\"type\":\"WORK_EMAIL\",\"email\":\"work@smith.com\"},{\"type\":\"PERSONAL_EMAIL\",\"email\":\"smith.john@mail.com\"}],\"phones\":[{\"type\":\"MOBILE_PHONE\",\"phone\":\"+7 (234) 4444343\"}]}";

        assertEquals(json, js.toJson(c1));
        assertEquals(c1, js.fromJson(json));

        // check for identity
        assertEquals(c1, js.fromJson(js.toJson(c1)));

        StoredCustomer sc1 = new StoredCustomer("123", c1);
        assertEquals(c1, js.fromJson(js.toJson(sc1)));
    }

}
