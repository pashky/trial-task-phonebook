package xml.phonebook.server;

import com.google.gson.*;
import xml.phonebook.model.*;
import xml.phonebook.storage.*;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for customers JSON (de)serialization
 *
 * @author pashky
 */
public class JsonHelper {
    /**
     * For GSON library
     */
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

    /**
     * For GSON library
     */
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

    /**
     * Thread-safe singleton
     */
    private static class Holder {
        private static final JsonHelper instance = new JsonHelper();
    }

    /**
     * Returns singleton helper instance
     * @return helper
     */
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

    /**
     * Convert from json string to customer object
     * @param json string
     * @return object
     */
    public Customer fromJson(String json) {
        return gson.fromJson(json, Customer.class);
    }

    /**
     * Convert either customer, stored customer or collection of such to json
     * @param customer something to serialize
     * @return JSON string
     */
    public String toJson(Object customer) {
        return gson.toJson(customer);
    }

    /**
     * Read customer object from reader
     * @param json reader
     * @return customer object
     */
    public Customer fromJson(Reader json) {
        return gson.fromJson(json, Customer.class);
    }

    /**
     * Write customer, stored collection or collection
     * @param customer something to serialize
     * @param json writer
     */
    public void toJson(Object customer, Writer json) {
        gson.toJson(customer, json);
    }

    public void toJsonTypes(Writer json) {
        Map<String,Collection<? extends AbstractType>> types = new HashMap<String, Collection<? extends AbstractType>>();
        types.put("phone", PhoneType.values());
        types.put("email", EmailType.values());
        types.put("address", AddressType.values());
        new Gson().toJson(types, json);
    }


    /**
     * Container class for JSON error result
     */
    private static class Error {
        private final String error;

        private Error(String error) {
            this.error = error;
        }
    }

    /**
     * Write JSON-encoded error message to writer
     * @param error error text
     * @param json writer
     */
    public void errorJson(String error, Writer json) {
        gson.toJson(new Error(error), json);
    }
}
