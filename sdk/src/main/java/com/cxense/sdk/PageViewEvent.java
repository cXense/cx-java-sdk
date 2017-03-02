package com.cxense.sdk;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;


public class PageViewEvent {

    private String siteId;
    private String userId;
    private String location;
    private String referrer;

    public PageViewEvent(String siteId, String location, String userId) {
        this.siteId = siteId;
        this.location = location;
        this.userId = userId;
    }
    public PageViewEvent setReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }

    public PageViewEvent setCustomParameter(String key, String value) {
        return this;
    }

    public int send() throws Exception {
        String baseUrl = "http://comcluster.cxense.com/Repo/rep.gif";
        String url = baseUrl +
                "?typ=pgv" +
                "&sid=" + URLEncoder.encode(this.siteId, "UTF-8") +
                "&loc=" + URLEncoder.encode(this.location, "UTF-8") +
                "&ckp=" + URLEncoder.encode(this.userId, "UTF-8") +
                "&rnd=" + ("" + Math.random() + new Date().getTime()).replace(".", "") +
                (this.referrer != null ? "%ref=" + URLEncoder.encode(this.referrer, "UTF-8") : "");
        System.out.println(url);
        HttpURLConnection connection = (HttpURLConnection) (new URL(url).openConnection());
        connection.connect();
        return connection.getResponseCode();
    }

}
