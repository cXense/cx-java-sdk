package com.cxense.sdk;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PageViewEvent {

    private String siteId;
    private String userId;
    private String location;
    private String referrer;
    private HashMap<String, String> customParameters = new HashMap<String, String>();
    private HashMap<String, String> externalIds = new HashMap<String, String>();

    public PageViewEvent(String siteId, String location, String userId) {
        this.siteId = siteId;
        this.location = location;
        this.userId = userId;
    }
    public PageViewEvent setReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }

    public PageViewEvent addCustomParameter(String key, String value) {
        customParameters.put(key, value);
        return this;
    }

    public PageViewEvent addCustomParameters(Map<String, String> newCustomParameters) {
        customParameters.putAll(newCustomParameters);
        return this;
    }

    public PageViewEvent addExternalUserId(String type, String id) {
        externalIds.put(type, id);
        return this;
    }

    public PageViewEvent addExternalUserIds(Map<String, String> newExternalIds) {
        externalIds.putAll(newExternalIds);
        return this;
    }

    public int send() throws Exception {
        String baseUrl = Cxense.pageViewEventBaseUrl;
        String url = baseUrl +
                "?typ=pgv" +
                "&sid=" + URLEncoder.encode(this.siteId, "UTF-8") +
                "&loc=" + URLEncoder.encode(this.location, "UTF-8") +
                "&ckp=" + URLEncoder.encode(this.userId, "UTF-8") +
                "&rnd=" + ("" + Math.random() + new Date().getTime()).replace(".", "") +
                (this.referrer != null ? "&ref=" + URLEncoder.encode(this.referrer, "UTF-8") : "");

        for (Map.Entry<String, String> entry : customParameters.entrySet()) {
            url += "&cp_" + URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
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