package utils;

import com.google.common.base.Strings;

import java.lang.management.MemoryUsage;


public enum StatisticFormatter {
    ;
    private static final String BAR_TRUE_CHARACTER = ":";
    private static final String BAR_FALSE_CHARACTER = ".";

    public static String generateMemoryUsageDiagram(MemoryUsage usage, int length) {

        double used = usage.getUsed();
        double committed = usage.getCommitted();
        double max = usage.getMax();

        int usedChars = (int) ((used * length) / max);
        int committedChars = (int) ((committed * length) / max);


        String result = "§e" + Strings.repeat(BAR_TRUE_CHARACTER, usedChars);

        if (committedChars > usedChars) {
            result += "§7" + Strings.repeat(BAR_FALSE_CHARACTER, (committedChars - usedChars) - 1);
            result += "§r" + BAR_FALSE_CHARACTER;
        }
        if (length > committedChars) {
            result += "§7" + Strings.repeat(BAR_FALSE_CHARACTER, (length - committedChars));
        }

        return "§8[" + result + "§8]";
    }

    public static String generateMemoryPoolDiagram(MemoryUsage usage, MemoryUsage collectionUsage, int length) {
        double used = usage.getUsed();
        double collectionUsed = used;
        if (collectionUsage != null) {
            collectionUsed = collectionUsage.getUsed();
        }
        double committed = usage.getCommitted();
        double max = usage.getMax();

        int usedChars = (int) ((used * length) / max);
        int collectionUsedChars = (int) ((collectionUsed * length) / max);
        int committedChars = (int) ((committed * length) / max);

        String line = "§e" + Strings.repeat(BAR_TRUE_CHARACTER, collectionUsedChars);

        if (usedChars > collectionUsedChars) {
            line += "§c" + BAR_TRUE_CHARACTER;
            line += "§e" + Strings.repeat(BAR_TRUE_CHARACTER, (usedChars - collectionUsedChars) - 1);
        }
        if (committedChars > usedChars) {
            line += "§7" + Strings.repeat(BAR_FALSE_CHARACTER, (committedChars - usedChars) - 1);
            line += "§e" + BAR_FALSE_CHARACTER;
        }
        if (length > committedChars) {
            line += "§7" + Strings.repeat(BAR_FALSE_CHARACTER, (length - committedChars));
        }

        return "§8[" + line + "§8]";
    }
}