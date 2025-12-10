package com.autobackupmod.core;

public class IntervalParser {
    public static long toMillis(String input) {
        char unit = input.charAt(input.length() - 1);
        long value = Long.parseLong(input.substring(0, input.length() - 1));

        return switch (unit) {
            case 's' -> value * 1000L;
            case 'm' -> value * 60_000L;
            case 'h' -> value * 3_600_000L;
            case 'd' -> value * 86_400_000L;
            case 'w' -> value * 604_800_000L;
            default -> 0;
        };
    }
}
