package com.adwheel.www.wheel.managers;

import java.util.Arrays;
import java.util.List;

public class AdTopicManager {

    private final PrefManager prefManager;

    private static final List<String> EXAMPLE_TOPICS = Arrays.asList(
            "technology",
            "toys",
            "video games",
            "clothing",
            "television shows",
            "sports"
    );

    public AdTopicManager(PrefManager prefManager) {
        this.prefManager = prefManager;
    }


    // TODO: implement to return a custom list of selectable topics.
    public List<String> getTopics() {
        return EXAMPLE_TOPICS;
    }
}
