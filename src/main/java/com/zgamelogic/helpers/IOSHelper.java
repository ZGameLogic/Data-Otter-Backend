package com.zgamelogic.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.serializable.iOS.iOSToken;

import java.io.File;
import java.io.IOException;

public abstract class IOSHelper {

    public static iOSToken loadToken(){
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(new File("token.json"), iOSToken.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveToken(iOSToken token){
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(new File("token.json"), token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sentNotification() throws JsonProcessingException {
//        String url = "https://api.development.push.apple.com:443/3/device/" + loadToken().getToken();
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("apns-topic", "zgamelogic.Monitors");
//        headers.add("apns-push-type", "alert");
//        headers.add("apns-priority", "5");
//        headers.add("apns-expiration", "z0");
//        headers.add("authorization", "bearer eyJhbGciOiJFUzI1NiIsImtpZCI6IjRMWDhYVjJNMjcifQ.eyJpc3MiOiI2TTM4Ujk3TDREIiwiaWF0IjoxNjkxNTM0MzY2fQ.w5Fo2CHtWXspKbQF15D84cptOHN9f_AMji7Ks1wbQs97PgCeZ4W8cKgym-wLCNo8Rc5tlSvGPvGwVALhoYC8Xw");
//
//        iOSNotification notification = new iOSNotification(new iOSNotification.APS("Data Otter", "Test notification", "notification body"));
//        String body = new ObjectMapper().writeValueAsString(notification);
//        HttpEntity<String> request = new HttpEntity<>(body, headers);
//        restTemplate.postForObject(url, request, String.class);
    }
}
