package com.zgamelogic.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class MonitorHistory {

    private Date taken;
    private boolean status;
    private long completedInMilliseconds;
    private int onlinePlayerCount;

}
