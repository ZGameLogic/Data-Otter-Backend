package com.zgamelogic.data.serializable.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public void addEntries(LinkedList<Entry> entries){
        if(entries == null) entries = new LinkedList<>();
        this.entries.addAll(entries);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Entry {
        private boolean status;
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Date time;
        private LinkedList<String> nodes;

        public Entry(boolean status, LinkedList<String> nodes){
            this.status = status;
            this.nodes = nodes;
            time = new Date();
        }

        public void addNode(String node){
            if(nodes == null) nodes = new LinkedList<>();
            nodes.add(node);
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
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

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
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