package com.zgamelogic.data.monitorHistory;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
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
public class MonitorStatus {
    @EmbeddedId
    private MonitorStatusId id;
    private boolean status;

    /**
     * User this when we have only the monitor id
     * @param id ID of the monitor configuration
     * @param status Status of the monitor
     */
    public MonitorStatus(Long id, boolean status){
        this.id = new MonitorStatusId(new MonitorConfiguration(id), new Date());
        this.status = status;
    }

    /**
     * Use this when we have the monitor configuration
     * @param monitor Monitor configuration the status data point is associated with
     * @param status Status of the monitor
     */
    public MonitorStatus(MonitorConfiguration monitor, boolean status) {
        this.id = new MonitorStatusId(monitor, new Date());
        this.status = status;
    }
}
