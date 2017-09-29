package com.cxense.sdk.app;

import com.cxense.sdk.Cxense;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Aleksey Tomin (aleksey.tomin@cxense.com) (2017-09-29)
 */
public class CxApp {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Must specify an api path");
            return;
        }
        String path = args[0];
        String params = "";
        if (args.length> 1) {
            params = args[1];
        }
        Cxense cxense;
        try {
            cxense = makeFromRcFile(System.getProperty("user.home") + "/.cxrc");
        } catch (IOException e) {
            cxense = new Cxense();
        }
        String jsonString = cxense.apiRequest(path, params);
        jsonString = new JSONObject(jsonString).toString(2);
        System.out.println(jsonString);

    }

    private static Cxense makeFromRcFile(String rcFileName) throws IOException {
        Reader fileReader = new FileReader(rcFileName);
        try {
            BufferedReader configReader = new BufferedReader(fileReader);
            try {
                String line;
                while ((line = configReader.readLine()) != null) {
                    if (line.startsWith("authentication ")) {
                        String[] authStringParts = line.split(" ");
                        if (authStringParts.length == 3) {
                            return new Cxense(authStringParts[1], authStringParts[2]);
                        } else {
                            throw new IOException("authentication string has wrong format - must be 'authentication user apiKey'");
                        }
                    }
                }
            } finally {
                configReader.close();
            }
        } finally {
            fileReader.close();
        }
        throw new IOException("authentication string not found");
    }


}
