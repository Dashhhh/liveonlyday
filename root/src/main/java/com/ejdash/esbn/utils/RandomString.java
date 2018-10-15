/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.utils;

import java.util.Random;

public class RandomString {


    public RandomString() {
    }

    public String Randomize(Integer length) {
        Random rnd = new Random();
        String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(str.charAt(rnd.nextInt(str.length())));
        }

        return sb.toString();
    }
}
