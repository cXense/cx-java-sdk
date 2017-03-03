package com.cxense.sdk;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class PageViewEventTest {
    private static String queryString;
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
        Cxense.pageViewEventBaseUrl = serverUri.resolve("/Repo/rep.gif").toString();
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
        int responseCode = Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").send();
        Assert.assertEquals(responseCode, HttpStatus.OK_200);
        queryString = queryString.replaceAll("rnd=[0-9]+", "rnd=1");
        Assert.assertEquals(queryString, "typ=pgv&sid=1234&loc=http%3A%2F%2Fwww.site.com%2F&ckp=abcd&rnd=1");
    }

    @Test
    public void testReferrer() throws Exception {
        String referrer = "http://www.siteb.com/";
        int responseCode = Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").setReferrer(referrer).send();
        queryString = queryString.replaceAll("rnd=[0-9]+", "rnd=1");
        Assert.assertEquals(queryString, "typ=pgv&sid=1234&loc=http%3A%2F%2Fwww.site.com%2F&ckp=abcd&rnd=1&ref=" + URLEncoder.encode(referrer, "UTF-8"));
    }

    @Test
    public void testCustomParameter() throws Exception {
        int responseCode = Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").addCustomParameter("section", "sports").send();
        queryString = queryString.replaceAll("rnd=[0-9]+", "rnd=1");
        Assert.assertEquals(queryString, "typ=pgv&sid=1234&loc=http%3A%2F%2Fwww.site.com%2F&ckp=abcd&rnd=1&cp_section=sports");
    }

    @Test
    public void testCustomParameters() throws Exception {
        Map<String, String> customParameters = new HashMap<String, String>() {{
            put("a", "1");
            put("b", "2");
            put("c", "3");
        }};
        int responseCode = Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").addCustomParameters(customParameters).send();
        queryString = queryString.replaceAll("rnd=[0-9]+", "rnd=1");
        Assert.assertEquals(queryString, "typ=pgv&sid=1234&loc=http%3A%2F%2Fwww.site.com%2F&ckp=abcd&rnd=1&cp_a=1&cp_b=2&cp_c=3");
    }

    @Test
    public void testExternalId() throws Exception {
        int responseCode = Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").addExternalUserId("fb", "ABCD").send();
        queryString = queryString.replaceAll("rnd=[0-9]+", "rnd=1");
        Assert.assertEquals(queryString, "typ=pgv&sid=1234&loc=http%3A%2F%2Fwww.site.com%2F&ckp=abcd&rnd=1&eit0=fb&eid0=ABCD");
    }

    @Test
    public void testExternalIds() throws Exception {
        Map<String, String> externalUserIds = new HashMap<String, String>() {{
            put("fbk", "1");
        }};
        int responseCode = Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").addExternalUserIds(externalUserIds).send();
        queryString = queryString.replaceAll("rnd=[0-9]+", "rnd=1");
        Assert.assertEquals(queryString, "typ=pgv&sid=1234&loc=http%3A%2F%2Fwww.site.com%2F&ckp=abcd&rnd=1&eit0=fbk&eid0=1");
    }

    public static class TestServlet extends HttpServlet {
        protected void doGet( HttpServletRequest request, HttpServletResponse response )
                throws ServletException, IOException {
            PageViewEventTest.queryString = request.getQueryString();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{}");
        }
    }
}
