package com.adwheel.www.wheel.models;

/**
 * Created by cbuonocore on 3/11/17.
 */

public class SoundFile {

    public String fileName;
    public String animal;
    public int listPosition;

    public SoundFile(String fileName, String animal, int listPosition) {
        this.fileName = fileName;
        this.animal = animal;
        this.listPosition = listPosition;
    }

    @Override
    public String toString() {
        return "SoundFile{" +
                "fileName='" + fileName + '\'' +
                ", animal='" + animal + '\'' +
                '}';
    }
}
