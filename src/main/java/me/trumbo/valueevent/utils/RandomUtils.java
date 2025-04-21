package me.trumbo.valueevent.utils;

import org.bukkit.Material;

import java.util.List;
import java.util.Random;

public final class RandomUtils {
    private static final Random RANDOM = new Random();

    private RandomUtils() {}

    public static int getRandomInt(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    public static int parseRandomRange(String rangeStr) {
        String[] range = rangeStr.split("-");
        if (range.length == 2) {
            try {
                int min = Integer.parseInt(range[0]);
                int max = Integer.parseInt(range[1]);
                return getRandomInt(min, max);
            } catch (NumberFormatException e) {
                return Integer.parseInt(range[0]);
            }
        }
        return Integer.parseInt(rangeStr);
    }
}