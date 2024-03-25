package utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static TextComponent buildTextComponent(String message, String color, String hover, ClickEvent.Action action, String action_data) {

        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(message));
        if (color != null) textComponent.setColor(net.md_5.bungee.api.ChatColor.of(color));
        if (hover != null)
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
        if (action != null) textComponent.setClickEvent(new ClickEvent(action, action_data));

        return textComponent;
    }

    public static TextComponent toTextComponent(String message) {
        if (message == null || !message.contains("§x")) return new TextComponent(message);

        TextComponent textComponent = new TextComponent();

        Pattern hexPattern = Pattern.compile("(§x(§[0-9A-Fa-f]){6})|(§[0-9abcdefonk])");
        Matcher matcher = hexPattern.matcher(message);

        boolean isFirst = false;
        int last = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            if (!isFirst) {
                textComponent.addExtra(parseColorToTextComponent(message.substring(0, start)));
                isFirst = true;
            }

            last = message.indexOf('§', end);
            last = last == -1 ? message.length() : last;

            textComponent.addExtra(parseColorToTextComponent(message.substring(start, last)));
        }

        textComponent.addExtra(parseColorToTextComponent(message.substring(last)));
        return textComponent;
    }

    private static TextComponent parseColorToTextComponent(String substring) {
        if (!substring.contains("§x")) return new TextComponent(substring);

        net.md_5.bungee.api.ChatColor chatColor = net.md_5.bungee.api.ChatColor.of("#" + substring.substring(2, 14).replaceAll("§", ""));

        TextComponent textComponent = new TextComponent(substring.substring(14));
        textComponent.setColor(chatColor);

        return textComponent;
    }

    public static String applyColors(String message) {
        return parseHEXColors(message).replaceAll("&", "§");
    }

    public static String parseHEXColors(String message) {
        return message.replaceAll("&#(?<a>[a-f0-9A-F])(?<b>[a-f0-9A-F])(?<c>[a-f0-9A-F])(?<d>[a-f0-9A-F])(?<e>[a-f0-9A-F])(?<f>[a-f0-9A-F])", "§x§${a}§${b}§${c}§${d}§${e}§${f}");
    }

    public static String getFormattedTime(long millis) {

        long years = millis >= 31536000000L ? TimeUnit.MILLISECONDS.toDays(millis) / 365 : 0;
        long days = millis >= 86400000L ? TimeUnit.MILLISECONDS.toDays(millis) % 365 : 0;
        long hours = millis >= 3600000L ? TimeUnit.MILLISECONDS.toHours(millis) % 24 : 0;
        long minutes = millis >= 60000L ? TimeUnit.MILLISECONDS.toMinutes(millis) % 60 : 0;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        StringBuilder stringBuilder = new StringBuilder();

        int valuesCount = 0;

        if (years != 0) {
            stringBuilder.append(formattedValue(years, "г", "г", "лет"));
            valuesCount++;
        }

        if (days != 0) {
            stringBuilder.append(formattedValue(days, "д", "д", "д"));
            valuesCount++;
        }

        if (hours != 0 && valuesCount < 2) {
            stringBuilder.append(formattedValue(hours, "ч", "ч", "ч"));
            valuesCount++;
        }

        if (minutes != 0 && valuesCount < 2) {
            stringBuilder.append(formattedValue(minutes, "м", "м", "м"));
            valuesCount++;
        }

        if (seconds != 0 && valuesCount < 2) {
            stringBuilder.append(formattedValue(seconds, "с", "с", "с"));
        }

        return stringBuilder.toString();
    }

    public static String formattedValue(long value, String val1, String val2, String val3) {
        return (value + (value % 100 != 11 && value % 10 == 1 ? val1 : value / 10 != 1 && value % 10 > 1 && value % 10 < 5 ? val2 : val3) + " ");
    }
}
