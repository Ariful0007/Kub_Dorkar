package com.example.kub_dorkar.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class EventDayPlan extends ExpandableGroup {

    public EventDayPlan(String title, List items) {
        super(title, items);
    }
}