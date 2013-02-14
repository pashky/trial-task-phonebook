package xml.phonebook.server;

import com.google.gson.*;
import xml.phonebook.model.*;
import xml.phonebook.storage.*;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created 14/02/2013 07:02
 *
 * @author pashky
 */
public class JsonHelper {
    private static class TypeJson implements JsonDeserializer<AbstractType>, JsonSerializer<AbstractType> {
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

    private static class StoredCustomerJson implements JsonSerializer<StoredCustomer>,JsonDeserializer<StoredCustomer> {
        public static StoredCustomerJson instance = new StoredCustomerJson();

        public StoredCustomer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext j) throws JsonParseException {
            Customer customer = j.deserialize(jsonElement, Customer.class);
            String id = jsonElement.getAsJsonObject().get("id").getAsString();
            return new StoredCustomer(id, customer);
        }

        public JsonElement serialize(StoredCustomer storedCustomer, Type type, JsonSerializationContext j) {
            JsonObject obj = j.serialize(storedCustomer.getCustomer()).getAsJsonObject();
            obj.addProperty("id", storedCustomer.getId());
            return obj;
        }
    }

    private static class Holder {
        private static final JsonHelper instance = new JsonHelper();
    }

    public static JsonHelper instance() {
        return Holder.instance;
    }

    private final Gson gson;

    private JsonHelper() {
        GsonBuilder gsonb = new GsonBuilder();
        gsonb.registerTypeAdapter(AddressType.class, TypeJson.instance);
        gsonb.registerTypeAdapter(EmailType.class, TypeJson.instance);
        gsonb.registerTypeAdapter(PhoneType.class, TypeJson.instance);
        gsonb.registerTypeAdapter(StoredCustomer.class, StoredCustomerJson.instance);
        gson = gsonb.create();
    }

    public Customer fromJson(String json) {
        return gson.fromJson(json, Customer.class);
    }

    public String toJson(Object customer) {
        return gson.toJson(customer);
    }

    public Customer fromJson(Reader json) {
        return gson.fromJson(json, Customer.class);
    }

    public void toJson(Object customer, Writer json) {
        gson.toJson(customer, json);
    }

    private static class Error {
        private final String error;

        private Error(String error) {
            this.error = error;
        }
    }

    public void errorJson(String error, Writer json) {
        gson.toJson(new Error(error), json);
    }
}
