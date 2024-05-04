package com.zgamelogic.data.monitorHistory;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Date;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MonitorStatusId {
    private long monitorId;
    private Date date;
}
