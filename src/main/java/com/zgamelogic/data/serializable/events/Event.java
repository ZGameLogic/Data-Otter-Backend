package com.zgamelogic.data.serializable.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zgamelogic.data.serializable.monitors.Monitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Event {
    private long monitorId;
    private String monitor;
    private LinkedList<Entry> entries;

    public Event(Monitor monitor){
        this(monitor.getId(), monitor.getName(), new LinkedList<>());
    }

    public void addEntry(Entry entry){
        if(entries == null) entries = new LinkedList<>();
        entries.add(entry);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Entry {
        private boolean status;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Date time;

        public Entry(boolean status){
            this.status = status;
            time = new Date();
        }
    }

    public Date getStartTime(){
        LinkedList<Date> dates = new LinkedList<>();
        for(Entry entry: entries){
            dates.add(entry.getTime());
        }
        if(!dates.isEmpty()) {
            Collections.sort(dates);
            return dates.getFirst();
        }
        return new Date();
    }

    public Date getEndTime(){
        LinkedList<Date> dates = new LinkedList<>();
        for(Entry entry: entries){
            dates.add(entry.getTime());
        }
        if(!dates.isEmpty()) {
            Collections.sort(dates);
            return dates.getLast();
        }
        return new Date();
    }
}