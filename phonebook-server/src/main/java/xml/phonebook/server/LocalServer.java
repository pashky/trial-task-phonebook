package xml.phonebook.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import xml.phonebook.storage.XmlStore;

import java.io.File;
import java.io.IOException;

/**
 * Embedded Jetty server class
 *
 * @author pashky
 */
public class LocalServer {
    public static void main(String[] args) throws Exception {
        XmlStore xmlStore = new XmlStore(new File(args.length > 0 ? args[0] :
                "/Users/pashky/dev/arcusys/phonebook/sample.xml"));
        try {
            try {
                xmlStore.read();
            } catch (IOException e) {
                // ignore, most likely it means file was not found
                // and subsequent write attempts may success
                e.printStackTrace();
            }

            xmlStore.setWriteErrorListener(new XmlStore.WriteErrorListener() {
                public void xmlWriteError(Throwable e) {
                    // report write error
                    // will continue to work with in-memory changes
                    e.printStackTrace();
                }
            });

            Server server = new Server(8888);

            ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
            server.setHandler(ctx);
            ctx.setContextPath("/");
            ctx.addServlet(ApiServlet.class, "/*");
            ctx.setAttribute(ApiServlet.ATTR_XML_STORE, xmlStore);

            ResourceHandler rsrc = new ResourceHandler();
            rsrc.setDirectoriesListed(true);
            rsrc.setWelcomeFiles(new String[] {"index.html"});

            rsrc.setResourceBase(LocalServer.class.getClassLoader().getResource("webapp").toExternalForm());

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] { rsrc, ctx });
            server.setHandler(handlers);

            server.start();
            server.join();
        } finally {
            xmlStore.shutdown();
        }
    }
}
