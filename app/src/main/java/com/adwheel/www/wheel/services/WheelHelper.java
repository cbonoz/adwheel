package com.adwheel.www.wheel.services;

import java.util.List;
import java.util.Random;

public class WheelHelper {
    private WheelHelper() {

    }

    public static <T> int getRandomIndex(List<T> arr) {
        Random rand = new Random();
        return rand.nextInt(arr.size() - 1);
    }

    public static int getRandomNumberOfRotations() {
        Random rand = new Random();
        return rand.nextInt(10) + 5;
    }
}
