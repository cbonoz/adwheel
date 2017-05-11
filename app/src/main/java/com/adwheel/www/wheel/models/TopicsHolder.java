package com.adwheel.www.wheel.models;

import java.util.ArrayList;
import java.util.List;

public class TopicsHolder {

    public ArrayList<String> topics;

    public TopicsHolder(List<String> topics) {
        // convert to support nonabstract methods after deserialization.
        this.topics = new ArrayList<>(topics);
    }

    public TopicsHolder() {
        // convert to support nonabstract methods after deserialization.
        this.topics = new ArrayList<>();
    }
}
