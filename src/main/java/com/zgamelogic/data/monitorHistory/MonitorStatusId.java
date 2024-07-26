package com.zgamelogic.data.monitorHistory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import jakarta.persistence.CascadeType;
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
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "MONITOR_ID", referencedColumnName = "MONITOR_CONFIGURATION_ID")
    private MonitorConfiguration monitor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
}
