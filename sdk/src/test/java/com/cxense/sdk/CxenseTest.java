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
    private static String authenticationString;
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
    public void testBasicsJsonString() throws Exception {
        Cxense cx = new Cxense("some.user@company.com", "api&user&h3Ke7wFEJcK33/dkeidj29==");
        String jsonResponseString = cx.apiRequest("/public/date", "{ \"a\": 1 }");
        Assert.assertTrue(("" + CxenseTest.authenticationString).matches(
                "^username=some\\.user@company\\.com date=20[0-9]+-[0-9]+-[0-9]+T[0-9]+:[0-9]+:[0-9.]+Z hmac-sha256-hex=[A-Z0-9]+$"));
        Assert.assertNull(CxenseTest.queryString);
        Assert.assertEquals(CxenseTest.jsonBody.getInt("a"), 1);
        Assert.assertEquals(jsonResponseString.trim(), "{\"date\":\"2017-03-06T15:31:52.511Z\"}");
    }

    @Test
    public void testBasicsJsonObject() throws Exception {
        Cxense cx = new Cxense("some.user@company.com", "api&user&h3Ke7wFEJcK33/dkeidj29==");
        String apiPath = "/public/date";
        JsonObject requestObject = Json.createObjectBuilder().add("b", 2).build();
        JsonObject responseObject = cx.apiRequest(apiPath, requestObject);
        Assert.assertTrue(("" + CxenseTest.authenticationString).matches(
                "^username=some\\.user@company\\.com date=20[0-9]+-[0-9]+-[0-9]+T[0-9]+:[0-9]+:[0-9.]+Z hmac-sha256-hex=[A-Z0-9]+$"));
        Assert.assertNull(CxenseTest.queryString);
        Assert.assertEquals(CxenseTest.jsonBody.getInt("b"), 2);
        Assert.assertEquals(responseObject.getString("date"), "2017-03-06T15:31:52.511Z");
    }

    @Test
    public void testPersistedQueryJsonObject() throws Exception {
        String persistedQueryId = "1234567890";
        String apiPath = "/public/date";
        Cxense cx = new Cxense();
        JsonObject requestObject = Json.createObjectBuilder().add("c", 3).build();
        JsonObject responseObject = cx.apiRequest(apiPath, requestObject, persistedQueryId);
        Assert.assertNull(CxenseTest.authenticationString);
        Assert.assertEquals(CxenseTest.queryString, "persisted=" + persistedQueryId );
        Assert.assertEquals(CxenseTest.jsonBody.getInt("c"), 3);
        Assert.assertEquals(responseObject.getString("date"), "2017-03-06T15:31:52.511Z");
    }

    public static class TestServlet extends HttpServlet {
        protected void doPost( HttpServletRequest request, HttpServletResponse response )
                throws ServletException, IOException {
            CxenseTest.authenticationString = request.getHeader("X-cXense-Authentication");
            CxenseTest.queryString = request.getQueryString();
            CxenseTest.jsonBody = Json.createReader(request.getReader()).readObject();

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{\"date\":\"2017-03-06T15:31:52.511Z\"}");
        }
    }
}
