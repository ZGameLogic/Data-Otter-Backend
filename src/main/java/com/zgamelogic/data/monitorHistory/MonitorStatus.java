package com.zgamelogic.data.monitorHistory;

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

    public MonitorStatus(long id, boolean status) {
        this.id = new MonitorStatusId(id, new Date());
        this.status = status;
    }
}
