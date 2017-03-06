package com.cxense.sdk;

import java.io.IOException;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class CxenseTest {
    private static String queryString;
    private static JsonObject jsonBody;
    private static Server server;

    @BeforeClass
    public static void startJetty() throws Exception {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(TestServlet.class, "/*");
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost();
        if (host == null) {
            host = "localhost";
        }
        int port = connector.getLocalPort();
        URI serverUri = new URI(String.format("http://%s:%d/", host, port));
        Cxense.baseUrl = serverUri.resolve("/").toString();
    }

    @AfterClass
    public static void stopJetty() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasics() throws Exception {
        Cxense cx = new Cxense("some.user@company.com", "api&user&h3Ke7wFEJcK33/dkeidj29==");
        String jsonResponseString = cx.apiRequest("/public/date", "{ }");
        Assert.assertEquals(jsonResponseString.trim(), "{\"date\":\"2017-03-06T15:31:52.511Z\"}");
    }

    public static class TestServlet extends HttpServlet {
        protected void doPost( HttpServletRequest request, HttpServletResponse response )
                throws ServletException, IOException {
            CxenseTest.queryString = request.getQueryString();
            CxenseTest.jsonBody = Json.createReader(request.getReader()).readObject();

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"date\":\"2017-03-06T15:31:52.511Z\"}");
        }
    }
}
