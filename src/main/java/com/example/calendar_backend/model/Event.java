package com.example.calendar_backend.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Event() {

    }

    public Event(String title, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTitle(){
        return title;
    }
    public LocalDate getDate(){
        return date;
    }
    public LocalTime getStartTime(){
        return startTime;
    }
    public LocalTime getEndTime(){
        return endTime;
    }

    public void setTitle(String t){
        this.title=t;
    }
    public void setDate(LocalDate d){
        this.date = d;
    }
    public void setStartTime(LocalTime s){
        this.startTime =s;
    }
    public void setEndTime(LocalTime e){
        this.endTime=e;
    }

}
