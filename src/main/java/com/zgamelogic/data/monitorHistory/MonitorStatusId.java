package com.zgamelogic.data.monitorHistory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MonitorStatusId {
    @MapsId
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({
            @JoinColumn(name = "MONITOR_CONFIGURATION_ID", referencedColumnName = "MONITOR_CONFIGURATION_ID"),
            @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "APPLICATION_ID")
    })
    private MonitorConfiguration monitor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
}
