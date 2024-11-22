package com.zgamelogic.data.repositories;

import com.zgamelogic.data.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {}
