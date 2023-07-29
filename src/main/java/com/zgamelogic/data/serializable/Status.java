package com.zgamelogic.data.serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
public class Status {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private final Date taken;
    private boolean status;
    private long completedInMilliseconds;

    public Status(){
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

}
