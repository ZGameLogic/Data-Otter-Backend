package com.zgamelogic.controllers;

import com.zgamelogic.data.devices.Device;
import com.zgamelogic.data.devices.DeviceRepository;
import com.zgamelogic.services.apns.ApplePushNotification;
import com.zgamelogic.services.apns.ApplePushNotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    private final ApplePushNotificationService applePushNotificationService;
    private final DeviceRepository deviceRepository;

    public NotificationController(ApplePushNotificationService applePushNotificationService, DeviceRepository deviceRepository) {
        this.applePushNotificationService = applePushNotificationService;
        this.deviceRepository = deviceRepository;
    }

    @PostMapping("notification")
    private void sendNotification(@RequestBody ApplePushNotification notification) {
        for (Device device : deviceRepository.findAll()) {
            applePushNotificationService.sendNotification(device.getId(), notification);
        }
    }
}
