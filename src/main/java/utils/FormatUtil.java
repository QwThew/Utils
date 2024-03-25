package utils;

import org.bukkit.ChatColor;

import java.util.Locale;

public class FormatUtil {

    private static final String[] SIZE_UNITS = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

    public static String percent(double value, double max) {
        double percent = (value * 100d) / max;
        return (int) percent + "%";
    }

    public static String formatBytes(long bytes) {
        if (bytes <= 0) {
            return "0 bytes";
        }
        int sizeIndex = (int) (Math.log(bytes) / Math.log(1024));
        return "§7" + String.format(Locale.ENGLISH, "%.1f", bytes / Math.pow(1024, sizeIndex))  + SIZE_UNITS[sizeIndex];
    }

    public static String formatBytes(long bytes, ChatColor color, String suffix) {
        String value;
        String unit;

        if (bytes <= 0) {
            value = "0";
            unit = "KB" + suffix;
        } else {
            int sizeIndex = (int) (Math.log(bytes) / Math.log(1024));
            value = String.format(Locale.ENGLISH, "%.1f", bytes / Math.pow(1024, sizeIndex));
            unit = SIZE_UNITS[sizeIndex] + suffix;
        }

        return color + value + " " + unit;
    }

    public static String formatSeconds(long seconds) {
        if (seconds <= 0) {
            return "0s";
        }

        long second = seconds;
        long minute = second / 60;
        second = second % 60;

        StringBuilder sb = new StringBuilder();
        if (minute != 0) {
            sb.append(minute).append("m ");
        }
        if (second != 0) {
            sb.append(second).append("s ");
        }

        return sb.toString().trim();
    }
}