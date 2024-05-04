package com.zgamelogic.data.monitorHistory;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.serialization.MonitorStatusSerialization;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "status_history")
@JsonSerialize(using = MonitorStatusSerialization.class)
public class MonitorStatus {
    @EmbeddedId
    private MonitorStatusId id;
    private long milliseconds;
    private boolean status;
    private int retries;

    /**
     * User this when we have only the monitor id
     * @param id ID of the monitor configuration
     * @param milliseconds Milliseconds it took to get this request
     * @param status Status of the monitor
     */
    public MonitorStatus(Long id, long milliseconds, boolean status, int retries){
        this(new MonitorConfiguration(id), milliseconds, status, retries);
    }

    /**
     * Use this when we have the monitor configuration
     * @param monitor Monitor configuration the status data point is associated with
     * @param milliseconds Milliseconds it took to get this request
     * @param status Status of the monitor
     */
    public MonitorStatus(MonitorConfiguration monitor, long milliseconds, boolean status, int retries) {
        this.id = new MonitorStatusId(monitor, new Date());
        this.milliseconds = milliseconds;
        this.status = status;
        this.retries = retries;
    }
}
