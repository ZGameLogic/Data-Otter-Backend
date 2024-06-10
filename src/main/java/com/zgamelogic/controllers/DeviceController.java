package com.zgamelogic.controllers;

import com.zgamelogic.data.devices.Device;
import com.zgamelogic.data.devices.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {
    private final DeviceRepository deviceRepository;

    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @PostMapping("/devices/register/{deviceId}")
    public ResponseEntity<?> registerDevice(@PathVariable String deviceId) {
        System.out.println("registering device " + deviceId);
        deviceRepository.save(new Device(deviceId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/devices/unregister/{deviceId}")
    public ResponseEntity<?> unregisterDevice(@PathVariable String deviceId) {
        System.out.println("unregistering device " + deviceId);
        deviceRepository.deleteById(deviceId);
        return ResponseEntity.ok().build();
    }
}
