package com.adwheel.www.wheel.services;

import java.util.List;
import java.util.Random;

public class WheelHelper {

    private final Random rand;

    public WheelHelper() {
        rand = new Random(System.currentTimeMillis());
    }

    public <T> int getRandomIndex(List<T> arr) {
        return rand.nextInt(arr.size() - 1);
    }

    public int getRandomNumberOfRotations() {
        return rand.nextInt(8) + 5;
    }
}
