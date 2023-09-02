package com.zgamelogic.data.serializable.monitors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zgamelogic.data.serializable.Status;
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
