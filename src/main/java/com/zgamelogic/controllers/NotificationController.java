package com.zgamelogic.controllers;

import com.zgamelogic.data.devices.Device;
import com.zgamelogic.data.devices.DeviceRepository;
import com.zgamelogic.services.apns.ApplePushNotification;
import com.zgamelogic.services.apns.ApplePushNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationController {
    private final ApplePushNotificationService applePushNotificationService;
    private final DeviceRepository deviceRepository;

    @PostMapping("notification")
    private void sendNotification(@RequestBody ApplePushNotification notification) {
        for (Device device : deviceRepository.findAll()) {
            applePushNotificationService.sendNotification(device.getId(), notification);
        }
    }
}
