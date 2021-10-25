package com.shade.isaac.util;

public class HashUtils {
    private HashUtils() {
    }

    public static int fnv1(CharSequence value) {
        int h = 0x5bb2220e;
        for (int i = 0; i < value.length(); i++) {
            h = h ^ (value.charAt(i) & 0xff);
            h = h * 0x1000193;
        }
        return h;
    }

    public static int djb2(CharSequence value) {
        int h = 5381;
        for (int i = 0; i < value.length(); i++) {
            h = h * 33 + (value.charAt(i) & 0xff);
        }
        return h;
    }
}
