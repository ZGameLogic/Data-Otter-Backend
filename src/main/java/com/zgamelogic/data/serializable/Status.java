package com.zgamelogic.data.serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date taken;
    private boolean status;
    private long completedInMilliseconds;

    @Setter
    private LinkedList<String> nodes;

    public void setup(){
        taken = new Date();
        completedInMilliseconds = System.currentTimeMillis();
    }

    public void setStatus(boolean status){
        this.status = status;
        finishedTaking();
    }

    private void finishedTaking(){
        completedInMilliseconds = System.currentTimeMillis() - completedInMilliseconds;
    }

    public void addNode(String node){
        if(nodes == null) nodes = new LinkedList<>();
        nodes.add(node);
    }

    public boolean softEquals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;
        Status status1 = (Status) o;
        return status == status1.status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;
        Status status1 = (Status) o;
        return status == status1.status && taken.equals(status1.taken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
