package ru.kalita.ratelimit.controllers;

import java.util.Random;

public class TestUtils {

    private final static Random RANDOM = new Random();
    private final static int IP_BOUND = 256;
    private final static String DOT = ".";

    public static String generateRandomIp() {
        return RANDOM.nextInt(IP_BOUND) + DOT +
                RANDOM.nextInt(IP_BOUND) + DOT +
                RANDOM.nextInt(IP_BOUND) + DOT +
                RANDOM.nextInt(IP_BOUND);
    }
}
