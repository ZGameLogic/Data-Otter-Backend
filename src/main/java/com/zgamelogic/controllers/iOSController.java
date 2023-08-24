package com.zgamelogic.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zgamelogic.data.serializable.iOS.iOSToken;
import com.zgamelogic.helpers.IOSHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class iOSController {

    @PostMapping("/ios/token")
    private void updateIOSToken(@RequestBody iOSToken token){
        IOSHelper.saveToken(token);
    }

    @GetMapping("test")
    private void test() throws JsonProcessingException {
        IOSHelper.sentNotification();
    }
}
