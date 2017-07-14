package com.cxense.sdk;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Page View Event model.
 *
 * @see <a href="https://wiki.cxense.com/display/cust/Event+data"></a> for full documentation
 */
public class PageViewEvent {
    /**
     * Identifier of the site for which pV-event will be reported.
     */
    private String siteId;
    /**
     * Identifier of the user.
     */
    private String userId;
    /**
     * URL of the site on which for which pV-event will be reported.
     */
    private String location;
    /**
     * Geographic latitude.
     */
    private Double latitude;
    /**
     * Geographic longitude.
     */
    private Double longitude;
    /**
     * The URL of the referring page.
     */
    private String referrer;
    /**
     * The screen resolution.
     */
    private String screenSize;
    /**
     * The initial browser window size.
     */
    private String windowSize;
    /**
     * Device pixel ratio (the ratio between physical and virtual pixels).
     * Typically read from window.devicePixelRatio in a browser.
     */
    private Double devicePixelRatio;
    /**
     * Custom parameters associated with current pV-event.
     */
    private HashMap<String, String> customParameters = new HashMap<String, String>();
    /**
     * User external identifiers associated with current pV-event.
     */
    private HashMap<String, String> externalIds = new HashMap<String, String>();

    /**
     * Create page view event instance.
     *
     * @param siteId   identifier of site on which event will be reported
     * @param location URL of the page for which pV-event is created
     * @param userId   identifier of the user
     */
    public PageViewEvent(String siteId,
                         String location,
                         String userId) {
        this.siteId = siteId;
        this.location = location;
        this.userId = userId;
    }

    /**
     * Set new referrer.
     *
     * @param referrer URL of the page-referrer
     * @return pV event instance
     */
    public PageViewEvent setReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }

    /**
     * Set new screen screen size.
     *
     * @param width  screen width
     * @param height screen height
     * @return pV event instance
     */
    public PageViewEvent setScreenSize(int width,
                                       int height) {
        this.screenSize = "" + width + "x" + height;
        return this;
    }

    /**
     * Set new window size.
     *
     * @param width  window width
     * @param height window height
     * @return pV event instance
     */
    public PageViewEvent setWindowSize(int width,
                                       int height) {
        this.windowSize = "" + width + "x" + height;
        return this;
    }

    /**
     * Set new device pixel ratio.
     *
     * @param devicePixelRatio ratio
     * @return pV event instance
     */
    public PageViewEvent setDevicePixelRatio(double devicePixelRatio) {
        this.devicePixelRatio = devicePixelRatio;
        return this;
    }

    /**
     * Set new geo position.
     *
     * @param latitude  geo latitude
     * @param longitude geo longitude
     * @return pV event instance
     */
    public PageViewEvent setGeoPosition(double latitude,
                                        double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }

    /**
     * Add new custom parameter.
     *
     * @param key   custom parameter's key
     * @param value custom parameter's value
     * @return pV event instance
     */
    public PageViewEvent addCustomParameter(String key,
                                            String value) {
        customParameters.put(key, value);
        return this;
    }

    /**
     * Add multiple custom parameters.
     *
     * @param newCustomParameters map of custom parameters
     * @return pV event instance
     */
    public PageViewEvent addCustomParameters(Map<String, String> newCustomParameters) {
        customParameters.putAll(newCustomParameters);
        return this;
    }

    /**
     * Add new external user id.
     *
     * @param type type of the identifier
     * @param id   external id
     * @return pV event instance
     */
    public PageViewEvent addExternalUserId(String type,
                                           String id) {
        externalIds.put(type, id);
        return this;
    }

    /**
     * Add multiple external identifiers.
     *
     * @param newExternalIds map of external identifiers
     * @return pV event instance
     */
    public PageViewEvent addExternalUserIds(Map<String, String> newExternalIds) {
        externalIds.putAll(newExternalIds);
        return this;
    }

    /**
     * Send pV event to back end.
     *
     * @return http response code
     * @throws Exception in case of problems during handling event's dispatch
     */
    public int send() throws Exception {
        String baseUrl = Cxense.pageViewEventBaseUrl;
        String url = baseUrl +
                "?typ=pgv" +
                "&sid=" + URLEncoder.encode(this.siteId, "UTF-8") +
                "&loc=" + URLEncoder.encode(this.location, "UTF-8") +
                "&ckp=" + URLEncoder.encode(this.userId, "UTF-8") +
                "&rnd=" + (Math.round(Math.random() * 2147483647)) +
                (this.screenSize != null ? "&res=" + URLEncoder.encode(this.screenSize, "UTF-8") : "") +
                (this.windowSize != null ? "&wsz=" + URLEncoder.encode(this.windowSize, "UTF-8") : "") +
                (this.devicePixelRatio != null ? "&dpr=" + URLEncoder
                        .encode(this.devicePixelRatio.toString(), "UTF-8") : "") +
                (this.latitude != null ? "&plat=" + URLEncoder.encode(this.latitude.toString(), "UTF-8") : "") +
                (this.longitude != null ? "&plon=" + URLEncoder.encode(this.longitude.toString(), "UTF-8") : "") +
                (this.referrer != null ? "&ref=" + URLEncoder.encode(this.referrer, "UTF-8") : "");

        for (Map.Entry<String, String> entry : customParameters.entrySet()) {
            url += "&cp_" + URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder
                    .encode(entry.getValue(), "UTF-8");
        }
        int i = 0;
        for (Map.Entry<String, String> entry : externalIds.entrySet()) {
            url += "&eit" + i + "=" + URLEncoder.encode(entry.getKey(), "UTF-8");
            url += "&eid" + i + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
            i++;
        }
        HttpURLConnection connection = (HttpURLConnection) (new URL(url).openConnection());
        connection.connect();
        return connection.getResponseCode();
    }

}
