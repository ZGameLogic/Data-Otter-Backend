package com.zgamelogic.data.serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Monitor {

    private int id;
    private String name;
    private String type;
    private LinkedList<Status> status;

    public void addMonitorStatus(Status status){
        if(this.status == null) this.status = new LinkedList<>();
        this.status.add(status);
    }

}
