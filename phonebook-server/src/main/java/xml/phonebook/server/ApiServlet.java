package xml.phonebook.server;

import xml.phonebook.model.Customer;
import xml.phonebook.storage.StoredCustomer;
import xml.phonebook.storage.XmlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Collection;

/**
 * Created 14/02/2013 07:19
 *
 * @author pashky
 */
public class ApiServlet extends HttpServlet {
    public static final String ATTR_XML_STORE = "xmlStore";

    private XmlStore getXmlStore() {
        return (XmlStore)getServletContext().getAttribute(ATTR_XML_STORE);
    }


    private void error(Writer output, String error) {
        JsonHelper.instance().errorJson(error, output);
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer output = new OutputStreamWriter(resp.getOutputStream(), "utf-8");
        Reader input = new InputStreamReader(req.getInputStream(), "utf-8");
        resp.setContentType("application/json; charset=utf-8");

        try {
            String path = req.getPathInfo();
            String search = req.getParameter("search");
            if(path.startsWith("/customers")) {
                String id = path.length() >= 11 ? URLDecoder.decode(path.substring("/customers/".length()), "utf-8") : "";
                if(!id.isEmpty() && "GET".equals(req.getMethod())) {
                    Customer customer = getXmlStore().findCustomerById(id);
                    if(customer != null) {
                        JsonHelper.instance().toJson(customer, output);
                    } else {
                        resp.setStatus(404);
                    }
                } else if("POST".equals(req.getMethod())) {
                    Customer customer = JsonHelper.instance().fromJson(input);
                    StoredCustomer stored = getXmlStore().addCustomer(customer);
                    JsonHelper.instance().toJson(stored, output);
                } else if(!id.isEmpty() && "PUT".equals(req.getMethod())) {
                    Customer customer = JsonHelper.instance().fromJson(input);
                    if(!getXmlStore().updateCustomerById(id, customer)) {
                        resp.setStatus(404);
                    } else {
                        JsonHelper.instance().toJson(customer, output);
                    }
                } else if(!id.isEmpty() && "DELETE".equals(req.getMethod())) {
                    if(!getXmlStore().deleteCustomerById(id))
                        resp.setStatus(404);
                } else if(search != null) {
                    Collection<StoredCustomer> customers = getXmlStore().findCustomersByText(search);
                    JsonHelper.instance().toJson(customers, output);
                } else {
                    Collection<StoredCustomer> customers = getXmlStore().findAllCustomers();
                    JsonHelper.instance().toJson(customers, output);
                }
            } else {
                resp.setStatus(404);
            }

        } catch (Throwable e) {
            resp.setStatus(500);
            error(output, "Exception: " + e);
        }

        output.flush();
    }

}
