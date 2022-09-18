package com.atquigu.rabbitmq.utils;

public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
