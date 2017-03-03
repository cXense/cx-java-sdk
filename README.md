# cx-java-sdk

## Features

The *Cxense Java SDK* enables Java developers to easily work with the Cxense APIs. You can get
started in minutes using ***Maven*** or by downloading a [single zip file][install-jar].

* [API Docs][docs-api]

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

### Send a basic page view event

```java
import com.cxense.sdk.Cxense;

public class SDKSample1 {
    public static void main(String[] args) throws Exception {
        
        //Cxense.pageViewEvent(<site ID>, <page url>, <end user ID>).send();
        Cxense.pageViewEvent("1234", "http://www.site.com/", "abcd").send();
        
    }
}
```

#### Send an advanced page view event

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

#### Send a page view event with maps of values

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

```java
import com.cxense.sdk.Cxense;

public class SDKSample4 {
    public static void main(String[] args) throws Exception {
        
        Cxense cx = new Cxense("<username (email)>", "<api key>");
        System.out.println( cx.apiRequest("/site", "{ }") );
        
    }
}
```




[homepage]: https://www.cxense.com/
[docs-api]: https://wiki.cxense.com/display/cust/Home
[install-jar]: http://sdk.cxense.com/cx-java-sdk-1.0.0.zip
[cx-java-sdk-bom]: https://github.com/cxense/cx-java-sdk/tree/master/cx-java-sdk-bom

