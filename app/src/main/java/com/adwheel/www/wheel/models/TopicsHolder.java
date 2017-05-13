package com.adwheel.www.wheel.models;

import java.util.ArrayList;
import java.util.List;

public class TopicsHolder {

    public long timestamp;
    public ArrayList<String> topics;

    public TopicsHolder(List<String> topics) {
        // convert to support nonabstract methods after deserialization.
        this.topics = new ArrayList<>(topics);
    }

    public TopicsHolder(List<String> topics, long timestamp) {
        // convert to support nonabstract methods after deserialization.
        this.topics = new ArrayList<>(topics);
        this.timestamp = timestamp;
    }

}
