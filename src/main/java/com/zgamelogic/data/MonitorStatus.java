package com.zgamelogic.data;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MonitorStatus {
    private Date time;
    private boolean status;
}
