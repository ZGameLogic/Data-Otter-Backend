package com.zgamelogic.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.serializable.iOSToken;

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
}
