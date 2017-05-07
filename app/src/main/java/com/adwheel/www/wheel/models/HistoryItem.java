package com.adwheel.www.wheel.models;

public class HistoryItem {
    public long timestamp;
    public String topics;

    public HistoryItem(long timestamp, String topics) {
        this.timestamp = timestamp;
        this.topics = topics;
    }
}
