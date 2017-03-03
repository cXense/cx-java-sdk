# cx-java-sdk

## Features

The *Cxense Java SDK* enables Java developers to easily work with the Cxense APIs. You can get
started in minutes using ***Maven*** or by downloading a [single zip file][install-jar].

## Getting Started

#### Sign up for Cxense ####

Before you begin, you need a Cxense account. Please see the [Cxense homepage][homepage] for information
about how to create a Cxense account and retrieve your Cxense credentials.

#### Install the SDK ####

##### Using the SDK with Maven #####

```xml
<dependencies>
  <dependency>
    <groupId>com.cxense.sdk</groupId>
    <artifactId>cx-java-sdk</artifactId>
  </dependency>
</dependencies>
```
##### Using the SDK with just one .jar file #####

Download the [single zip file][install-jar] and add the .jar file to the classpath. 

##### Building From Source

Check out the code from GitHub and build it using Maven.

```
mvn clean package
```

## Sample usage

### Send a page view event

#### Send a basic page view event

```java
import com.cxense.sdk.Cxense;

public class SDKSample1 {
    public static void main(String[] args) throws Exception {
        
        //Cxense.pageViewEvent(<site ID>, <page url>, <end user ID>).send();
        Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").send();
        
    }
}
```

##### Send an advanced page view event

```java
import com.cxense.sdk.Cxense;

public class SDKSample2 {
    public static void main(String[] args) throws Exception {
        
        //Cxense.pageViewEvent(<site ID>, <page url>, <end user ID>);
        Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd")
            .setReferrer("http://www.siteb.com/")
            .setScreenSize(1024, 768)
            .setWindowSize(927, 349)
            .setDevicePixelRatio(1.4)
            .setGeoPosition(7.6145, 110.7122)
            .addCustomParameter("section", "sports")
            .addExternalUserId("fbk", "ABCD") // Add FaceBook user ID
            .send();
    }
}
```

##### Send a page view event with maps of values

For custom parameters and external IDs, you can provide a map of values instead of individual values:

```java
import com.cxense.sdk.Cxense;

public class SDKSample3 {
    public static void main(String[] args) throws Exception {

        Map<String, String> customParameters = new HashMap<String, String>() {{
            put("a", "1");
            put("b", "2");
            put("c", "3");
        }};

        Map<String, String> externalUserIds = new HashMap<String, String>() {{
            put("fbk", "1234");
            put("slk", "ABCD");
        }};
        
        //Cxense.pageViewEvent(<site ID>, <page url>, <end user ID>);
        Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd")
            .addCustomParameters(customParameters)
            .addExternalUserIds(externalUserIds)
            .send();
    }
}
```

### Generic API requests

##### Request and response as JSON strings

A generic API request sending the request as a JSON string and getting the response back as a JSON string 

```java
import com.cxense.sdk.Cxense;

public class SDKSample4 {
    public static void main(String[] args) throws Exception {
        
        Cxense cx = new Cxense("<username (email)>", "<api key>");
        String apiPath = "/site";
        String request = "{}";
        String response = cx.apiRequest(apiPath, request);
        
    }
}
```

##### Request and response as JSON objects

A generic API request sending the request as a JSON object and getting the response back as a JSON object 

```java
import com.cxense.sdk.Cxense;
import javax.json.Json;
import javax.json.JsonObject;

public class SDKSample5 {
    public static void main(String[] args) throws Exception {
        
        Cxense cx = new Cxense("<username (email)>", "<api key>");
        String apiPath = "/site";
        JsonObject requestObject = Json.createObjectBuilder().build();
        JsonObject responseObject = cx.apiRequest(apiPath, requestObject);
        System.out.println(responseObject.toString());
        
    }
}
```

##### Using a custom HTTP client

If you want to use a custom HTTP client, there is a helper method that will build the required authentication headers for you.
Here is an example using Java's URLConnection:

```java
import com.cxense.sdk.Cxense;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SDKSample6 {
    public static void main(String[] args) throws Exception {
        
        String username = "<username (email)>";
        String apiKey = "<api key>";
        String apiBaseUrl = "https://api.cxense.com";
        String apiPath = "/site";
        String jsonStringQuery = "{}";

        String encodedJsonQuery = URLEncoder.encode(jsonStringQuery, "UTF-8");
        URLConnection connection = new URL(apiBaseUrl + apiPath + "?json=" + encodedJsonQuery).openConnection();
        connection.setRequestProperty("X-cXense-Authentication", Cxense.getHttpAuthenticationHeader(username, apiKey));
        connection.connect();
        String jsonResponse = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        System.out.println(jsonResponse );
        
    }
}
```


## Resources

* [API Docs][docs-api]




[homepage]: https://www.cxense.com/
[docs-api]: https://wiki.cxense.com/display/cust/Home
[install-jar]: http://sdk.cxense.com/cx-java-sdk-1.0.0.zip
[cx-java-sdk-bom]: https://github.com/cxense/cx-java-sdk/tree/master/cx-java-sdk-bom

