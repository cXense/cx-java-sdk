package com.cxense.sdk;

import static com.cxense.sdk.consts.Constants.BASE_URL;
import static com.cxense.sdk.consts.Constants.DEFAULT_CONNECTION_TIMEOUT_MS;
import static com.cxense.sdk.consts.Constants.DEFAULT_READ_TIMEOUT_MS;
import static com.cxense.sdk.consts.Constants.DEFAULT_USER_AGENT;
import static com.cxense.sdk.utils.AuthUtility.getHttpAuthenticationHeader;

import org.apache.commons.io.IOUtils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Main class of the SDK through which all settings related to
 * API communications can be set.
 */
@SuppressWarnings({"unused", "Since15", "SameParameterValue"})
public class Cxense {
    /**
     * Base URL to which requests will be send.
     * Can be overridden.
     */
    static String baseUrl;
    /**
     * Base URL to which page view events will be dispatched.
     * Can be overridden.
     */
    static String pageViewEventBaseUrl;

    static {
        // Initialize base URL with default url
        baseUrl = BASE_URL;
    }

    /**
     * Name of the user under which API will be called.
     */
    private final String username;
    /**
     * API key of the user under which API will be called.
     */
    private final String apiKey;
    /**
     * User-Agent string that will be sent as part of http request.
     * <p>
     * <b>Note:</b>
     * Incorrect or custom (unsupported by back end) UA string can lead
     * to the situation when reported page view events will not be available
     * in Insight.
     */
    private String userAgent;
    /**
     * Amount of milliseconds to wait for established connection.
     */
    private long connectTimeoutMillis;
    /**
     * Amount of milliseconds to wait for packets to be read from the network.
     */
    private long readTimeoutMillis;

    {
        // Initialize vital settings with defaults
        userAgent = DEFAULT_USER_AGENT;
        connectTimeoutMillis = DEFAULT_CONNECTION_TIMEOUT_MS;
        readTimeoutMillis = DEFAULT_READ_TIMEOUT_MS;
    }

    public Cxense() {
        this(null, null);
    }

    public Cxense(String username,
                  String apiKey) {
        this.username = username;
        this.apiKey = apiKey;
    }

    /**
     * Create new instance of PageViewEvent with specific parameters.
     *
     * @param siteId   identifier of the site to which pV-event is related
     * @param location URL of the page to which pV-event is related
     * @param userId   identifier of the user
     * @return new instance of PageViewEvent
     */
    public static PageViewEvent pageViewEvent(String siteId,
                                              String location,
                                              String userId) {
        return new PageViewEvent(siteId, location, userId);
    }

    /**
     * Set new User-Agent string.
     * <p>
     * <b>Note:</b>
     * Default UA string will be overridden by new value.
     *
     * @param userAgent new UA string
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Set new connection timeout in millis.
     * <p>
     * <b>Note:</b>
     * Default network connection timeout will be overridden by new value.
     *
     * @param millis new connection timeout
     */
    public void setConnectTimeoutMillis(long millis) {
        this.connectTimeoutMillis = millis;
    }

    /**
     * Set new read timeout in millis.
     * <p>
     * <b>Note:</b>
     * Default socket read timeout will be overridden by new value.
     *
     * @param millis new read timeout
     */
    public void setReadTimeoutMillis(long millis) {
        this.readTimeoutMillis = millis;
    }

    /**
     * Perform API request to specific service with specific query.
     *
     * @param apiPath   path to specific API service to which request must be send
     * @param jsonQuery specific query
     * @return API response in json format
     * @throws Exception in case of API communication failures
     */
    public String apiRequest(String apiPath,
                             String jsonQuery) throws Exception {
        return apiRequest(apiPath, jsonQuery, null);
    }

    /**
     * Perform API request to specific service with specific query.
     *
     * @param apiPath          path to specific API service to which request must be send
     * @param jsonQuery        specific query
     * @param persistedQueryId identifier of persistent query to be used
     * @return API response in json format
     * @throws Exception in case of API communication failures
     */
    public String apiRequest(String apiPath,
                             String jsonQuery,
                             String persistedQueryId) throws Exception {
        if (persistedQueryId == null && (this.username == null || this.apiKey == null)) {
            throw new IllegalArgumentException("Username + apiKey OR a persistedQueryId must be supplied.");
        }
        byte[] jsonQueryBytes = jsonQuery.getBytes("UTF-8");
        String url = baseUrl + apiPath +
                (persistedQueryId != null ? "?persisted=" + URLEncoder.encode(persistedQueryId, "UTF-8") : "");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", this.userAgent);
        connection.setRequestProperty("Content-Length", "" + jsonQueryBytes.length);
        if (persistedQueryId == null) {
            connection.setRequestProperty("X-cXense-Authentication",
                                          getHttpAuthenticationHeader(this.username, this.apiKey));
        }
        connection.setConnectTimeout((int) this.connectTimeoutMillis);
        connection.setReadTimeout((int) this.readTimeoutMillis);
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
            IOUtils.closeQuietly(inputStream, outputStream);
        }
        return jsonResponse.toString();
    }

    /**
     * Perform API request to specific service with specific json request.
     *
     * @param apiPath     path to specific API service to which request must be send
     * @param jsonRequest specific json request
     * @return API response descriptor
     * @throws Exception in case of API communication failures
     */
    public JsonObject apiRequest(String apiPath,
                                 JsonObject jsonRequest) throws Exception {
        return apiRequest(apiPath, jsonRequest, null);
    }

    /**
     * Perform API request to specific service with specific json request.
     *
     * @param apiPath          path to specific API service to which request must be send
     * @param jsonRequest      specific json request
     * @param persistedQueryId identifier of persistent query to be used
     * @return API response descriptor
     * @throws Exception in case of API communication failures
     */
    public JsonObject apiRequest(String apiPath,
                                 JsonObject jsonRequest,
                                 String persistedQueryId) throws Exception {
        String jsonStringResponse = this.apiRequest(apiPath, jsonRequest.toString(), persistedQueryId);
        JsonReader jsonReader = Json.createReader(new StringReader(jsonStringResponse));
        try {
            return jsonReader.readObject();
        } finally {
            jsonReader.close();
        }
    }

}
