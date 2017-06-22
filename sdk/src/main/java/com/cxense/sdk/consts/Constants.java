package com.cxense.sdk.consts;

import java.util.concurrent.TimeUnit;

/**
 * @author Anver Bogatov (anver.bogatov@cxense.com) (2017-06-22)
 */
public final class Constants {
    public static final String BASE_URL = "https://api.cxense.com";
    public static final String PAGE_VIEW_EVENT_BASE_URL2 = "http://comcluster.cxense.com/Repo/rep.gif";

    public static final String DEFAULT_USER_AGENT = "cx-java-sdk/1.0";

    public static final long DEFAULT_CONNECTION_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(20);
    public static final long DEFAULT_READ_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5);

    private Constants() {
        // Should be never invoked since it just constants holder
    }
}
