package com.pivotal.cf.broker.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Utility methods
 *
 * Created by honine on 3/27/14.
 */
public class Utils {

    public static String generateRandomUsername(int length) {
        return UUID.randomUUID().toString().substring(0,length);
    }

    public static String reverse(String s) {
        return new StringBuffer(s).reverse().toString();
    }

    // get the (first) application URI from the environment (in case of a PCF deployment...)
    public static String getAppUri() {
        String appUri = null;
        String vcap_app = System.getenv("VCAP_APPLICATION");
        if(vcap_app == null || vcap_app.length() == 0) {
            appUri = "localhost";
        }
        else {
            Map vcap_app_map = jsonToMap(vcap_app);
            appUri = ((List<String>)vcap_app_map.get("application_uris")).get(0);
        }
        return appUri;
    }

    /*
     * construct JSON string from map
     */
    public static String mapToJson(Map<String,Object> map) {
        String json = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(map);
        }
        catch(Exception e) {
        }
        return json;
    }

    /*
     * construct map from JSON representation
     */
    public static Map<String,Object> jsonToMap(String json) {
        Map<String,Object> m = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            m = mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
        }
        catch (Exception e) {
        }
        return m;
    }

}
