# cx-java-sdk
Cxense Java SDK

The *Cxense Java SDK* enables Java developers to easily work with the Cxense APIs. You can get
started in minutes using ***Maven*** or by downloading a [single zip file][install-jar].

* [API Docs][docs-api]


## Getting Started

#### Sign up for Cxense ####

Before you begin, you need a Cxense account. Please see the [Cxense homepage][homepage] for information
about how to create a Cxense account and retrieve your Cxense credentials.

#### Minimum requirements ####

To run the SDK you will need **Java 1.6+**.

#### Install the SDK ####

The recommended way to use the Cxense Java SDK in your project is to consume it from Maven. Import
the [cx-java-sdk-bom][] and specify the sdk Maven module in the dependencies.

##### Importing the BOM #####

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.cxense.sdk</groupId>
      <artifactId>cx-java-sdk</artifactId>
      <version>1.0.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

##### Using the SDK Maven modules #####

```xml
<dependencies>
  <dependency>
    <groupId>com.cxense.sdk</groupId>
    <artifactId>cx-java-sdk</artifactId>
  </dependency>
</dependencies>
```

## Sample usage

```java
import com.cxense.sdk.Cxense;

public class SDKSample1 {
    public static void main(String[] args) throws Exception {
        
        Cxense cx = new Cxense("<username (email)>", "<api key>");
        System.out.println( cx.apiRequest("/site", "{ }") );
        
    }
}
```



## Features

* Provides an easy-to-use client for Cxense services.


## Building From Source

Once you check out the code from GitHub, you can build it using Maven.

```
mvn clean package
```


[homepage]: https://www.cxense.com/
[docs-api]: https://wiki.cxense.com/display/cust/Home
[install-jar]: http://sdk.cxense.com/cx-java-sdk-1.0.0.zip
[cx-java-sdk-bom]: https://github.com/cxense/cx-java-sdk/tree/master/cx-java-sdk-bom

