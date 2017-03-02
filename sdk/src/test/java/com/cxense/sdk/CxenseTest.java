package com.cxense.sdk;

import java.io.IOException;
import java.net.URI;
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


public class CxenseTest {
    private static String queryString;
    private static Server server;
    private static URI serverUri;

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
        serverUri = new URI(String.format("http://%s:%d/",host,port));
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
    public void testSendPageViewEvent() throws Exception {
        Cxense.pageViewEventBaseUrl = serverUri.resolve("/Repo/rep.gif").toString();
        int responseCode = Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").send();
        Assert.assertEquals(responseCode, HttpStatus.OK_200);
        queryString = queryString.replaceAll("rnd=[0-9]+", "rnd=1");
        Assert.assertEquals(queryString, "typ=pgv&sid=1234&loc=http%3A%2F%2Fwww.site.com%2F&ckp=abcd&rnd=1");
    }

    public static class TestServlet extends HttpServlet {
        protected void doGet( HttpServletRequest request, HttpServletResponse response )
                throws ServletException, IOException {
            CxenseTest.queryString = request.getQueryString();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{}");
        }
    }
}
