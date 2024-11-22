package com.zgamelogic.data.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.serialization.MonitorStatusSerialization;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@ToString
@Table(name = "status_history")
@JsonSerialize(using = MonitorStatusSerialization.class)
public class MonitorStatus {
    @EmbeddedId
    private MonitorStatusId id;
    private long milliseconds;
    private boolean status;
    private int attempts;
    private int statusCode;

    /**
     * User this when we have only the monitor id
     * @param id ID of the monitor configuration
     * @param milliseconds Milliseconds it took to get this request
     * @param status Status of the monitor
     * @param attempts Number of retries it took
     * @param statusCode Status code of the call
     */
    public MonitorStatus(Long id, long milliseconds, boolean status, int attempts, int statusCode){
        this(new MonitorConfiguration(id, 0L), milliseconds, status, attempts, statusCode);
    }

    /**
     * Use this when we have the monitor configuration
     * @param monitor Monitor configuration the status data point is associated with
     * @param milliseconds Milliseconds it took to get this request
     * @param status Status of the monitor
     * @param attempts Number of retries it took
     * @param statusCode Status code of the call
     */
    public MonitorStatus(MonitorConfiguration monitor, long milliseconds, boolean status, int attempts, int statusCode) {
        this.id = new MonitorStatusId(monitor, new Date());
        this.milliseconds = milliseconds;
        this.status = status;
        this.attempts = attempts;
        this.statusCode = statusCode;
    }

    public MonitorStatus(MonitorConfiguration monitor, NodeMonitorReport report){
        this.id = new MonitorStatusId(monitor, new Date());
        this.milliseconds = report.getMilliseconds();
        this.status = report.isStatus();
        this.attempts = report.getAttempts();
        this.statusCode = report.getStatusCode();
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class MonitorStatusId {
        @MapsId
        @ManyToOne(cascade = CascadeType.ALL)
        @JoinColumns({
                @JoinColumn(name = "MONITOR_CONFIGURATION_ID", referencedColumnName = "MONITOR_CONFIGURATION_ID"),
                @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "APPLICATION_ID")
        })
        private MonitorConfiguration monitor;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date date;
    }
}
