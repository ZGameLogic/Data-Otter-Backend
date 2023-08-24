package com.zgamelogic.data.serializable.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.LinkedList;

@Getter
@Setter
@ToString
public class Events {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date time;
    private LinkedList<Event> events;

    public Events(){
        time = new Date();
        events = new LinkedList<>();
    }

    public void addEvent(Event event){
        events.add(event);
    }

    public boolean hasEvents(){
        return !events.isEmpty();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Event {
        private String monitor;
        private boolean status;
    }
}
