package com.adwheel.www.wheel.models;

import java.util.ArrayList;
import java.util.List;

public class WheelTopics {

    public ArrayList<String> topics;

    public WheelTopics(List<String> topics) {
        // convert to support nonabstract methods after deserialization.
        this.topics = new ArrayList<>(topics);
    }

}
