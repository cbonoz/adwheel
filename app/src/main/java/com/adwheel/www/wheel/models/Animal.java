package com.adwheel.www.wheel.models;

import java.util.Date;

/**
 * Created by cbuonocore on 3/11/17.
 */

public class Animal {

    public String userId;
    public String username;
    public String animal;
    public int count = 0;
    public Date created;

    public Animal(String userId, String username, String animal, int count, Date created) {
        this.userId = userId;
        this.username = username;
        this.animal = animal;
        this.count = count;
        this.created = created;
    }

//    public String printUserCount() {
//        return username + ": " + count + " times";
//    }
//
//    public String printAnimalCount() {
//        return animal + ": " + count + " times";
//    }
}
