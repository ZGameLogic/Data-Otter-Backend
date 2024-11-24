package com.zgamelogic.controllers;

import com.zgamelogic.data.components.DynamicDeviceRepository;
import com.zgamelogic.data.entities.Device;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {
    private final DynamicDeviceRepository deviceRepository;

    public DeviceController(DynamicDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @PostMapping("/devices/register/{deviceId}")
    public ResponseEntity<?> registerDevice(@PathVariable String deviceId) {
        deviceRepository.save(new Device(deviceId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/devices/unregister/{deviceId}")
    public ResponseEntity<?> unregisterDevice(@PathVariable String deviceId) {
        deviceRepository.deleteById(deviceId);
        return ResponseEntity.ok().build();
    }
}
