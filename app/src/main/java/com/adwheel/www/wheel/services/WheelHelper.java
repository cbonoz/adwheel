package com.adwheel.www.wheel.services;

import java.util.List;
import java.util.Random;

public class WheelHelper {

    private static final int BASE_SPINS = 5;

    private final Random rand;

    public WheelHelper() {
        rand = new Random();
    }

    public <T> int getRandomIndex(List<T> arr) {
        // reseed the random index.
        rand.setSeed(System.currentTimeMillis());
        return rand.nextInt(arr.size() - 1);
    }

    public int getRandomNumberOfRotations() {
        rand.setSeed(System.currentTimeMillis());
        return rand.nextInt(BASE_SPINS) + BASE_SPINS;
    }
}
