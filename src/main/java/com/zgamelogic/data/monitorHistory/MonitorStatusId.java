package com.zgamelogic.data.monitorHistory;

import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Date;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MonitorStatusId {
    @ManyToOne
    @JoinColumn(name = "MONITOR_ID", referencedColumnName = "ID")
    private MonitorConfiguration monitor;
    private Date date;
}
