package com.cxense.sdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.bind.DatatypeConverter;


public class Cxense {
    private String username;
    private String apiKey;
    protected static String baseUrl = "https://api.cxense.com";
    private String userAgent = "cx-java-sdk/1.0";
    private int connectTimeoutMillis = 1000 * 20; // 20 seconds
    private int readTimeoutMillis = 1000 * 60 * 5; // 5 minutes
    protected static String pageViewEventBaseUrl = "http://comcluster.cxense.com/Repo/rep.gif";

    // Default constructor
    public Cxense() { }

    // Constructor
    public Cxense(String username, String apiKey) {
        this.username = username;
        this.apiKey = apiKey;
    }

    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public void setConnectTimeoutMillis(int millis) { this.connectTimeoutMillis = millis; }

    public void setReadTimeoutMillis(int millis) { this.readTimeoutMillis = millis; }

    public static String getHttpAuthenticationHeader(String username, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        String dateString = "" + Instant.now();
        byte[] signature = mac.doFinal(dateString.getBytes("UTF-8"));
        return "username=" + username + " date=" + dateString + " hmac-sha256-hex=" + DatatypeConverter.printHexBinary(signature);
    }

    public static String getRandomId() {
        return System.currentTimeMillis() + "" + Math.round(Math.random() * 10000000);
    }

    public String apiRequest(String apiPath, String jsonQuery) throws Exception {
        return apiRequest(apiPath, jsonQuery, null);
    }

    public String apiRequest(String apiPath, String jsonQuery, String persistedQueryId) throws Exception {
        if (persistedQueryId == null && (this.username == null || this.apiKey == null))
            throw new IllegalArgumentException("Username + apiKey OR a persistedQueryId must be supplied.");
        byte[] jsonQueryBytes = jsonQuery.getBytes("UTF-8");
        String url = this.baseUrl + apiPath +
                (persistedQueryId != null ? "?persisted=" + URLEncoder.encode(persistedQueryId, "UTF-8") : "");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", this.userAgent);
        connection.setRequestProperty("Content-Length", "" + jsonQueryBytes.length);
        if (persistedQueryId == null) {
            connection.setRequestProperty("X-cXense-Authentication", getHttpAuthenticationHeader(this.username, this.apiKey));
        }
        connection.setConnectTimeout(this.connectTimeoutMillis);
        connection.setReadTimeout(this.readTimeoutMillis);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        StringBuilder jsonResponse = new StringBuilder();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            connection.connect();
            outputStream = connection.getOutputStream();
            outputStream.write(jsonQueryBytes);
            inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line).append("\n");
            }
        } finally {
            try { if (inputStream != null) { inputStream.close(); } } catch(Exception e) { };
            try { if (outputStream != null) { outputStream.close(); } } catch(Exception e) { };
        }
        return jsonResponse.toString();
    }

    public JsonObject apiRequest(String apiPath, JsonObject jsonRequest) throws Exception {
        return apiRequest(apiPath, jsonRequest, null);
    }

    public JsonObject apiRequest(String apiPath, JsonObject jsonRequest, String persistedQueryId) throws Exception {
        String jsonStringResponse = this.apiRequest(apiPath, jsonRequest.toString(), persistedQueryId);
        JsonReader jsonReader = Json.createReader(new StringReader(jsonStringResponse));
        return jsonReader.readObject();
    }

    public static PageViewEvent pageViewEvent(String siteId, String location, String userId) {
        return new PageViewEvent(siteId, location, userId);
    }

}
