package com.example.kub_dorkar.model;

public class EventPerDay {

    private int day;
    private String timeFrom;
    private String timeTo;
    private String name;

    public EventPerDay(int day, String timeFrom, String timeTo, String name) {
        this.day = day;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
