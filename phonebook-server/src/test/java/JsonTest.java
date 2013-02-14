import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xml.phonebook.model.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import static org.junit.Assert.fail;

/**
 * Created 14/02/2013 05:51
 *
 * @author pashky
 */
public class JsonTest {

    public static class TypeJson implements JsonDeserializer<AbstractType>, JsonSerializer<AbstractType> {
        public static TypeJson instance = new TypeJson();
        public AbstractType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext j) throws JsonParseException {
            try {
                @SuppressWarnings("unchecked")
                Method mm = ((Class)type).getDeclaredMethod("valueOf", String.class);
                Object o = mm.invoke(type, jsonElement.getAsJsonPrimitive().getAsString());
                return (AbstractType)o;
            } catch(Exception e) {
                throw new JsonParseException("Can't map type");
            }
        }

        public JsonElement serialize(AbstractType addressType, Type type, JsonSerializationContext j) {
            return new JsonPrimitive(addressType.getName());
        }
    }

    @Test
    public void testGson()
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

        GsonBuilder gson = new GsonBuilder();

        gson.registerTypeAdapter(AddressType.class, TypeJson.instance);
        gson.registerTypeAdapter(EmailType.class, TypeJson.instance);
        gson.registerTypeAdapter(PhoneType.class, TypeJson.instance);

        String json = "{\"type\":\"OTHER_ADDRESS\",\"streetLines\":[\"Corp Building\",\"123 High Street\"],\"postalCode\":\"455778\",\"town\":\"Los Angeles\"}";
        Gson gs = gson.create();
        Object address = gs.fromJson(json, Address.class);
        System.out.println(address.toString());
        System.out.println(StringUtils.join(AddressType.values(), ", "));
        //fail(address.toString());
        fail(gs.toJson(c1));
    }

}
