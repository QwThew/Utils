package utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.gc.GarbageCollector;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.Gradient.Rainbow.createGradient;


public class PlaceHolder extends PlaceholderExpansion {
    private final ArrayList<String> gradient;
    private final Spark spark;

    public PlaceHolder(Plugin pl) {

        gradient = createGradient(100, new String[]{"#81ff00", "#ffff00", "#ff4400"});
        spark = SparkProvider.get();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Valless";
    }

    @Override
    public String getIdentifier() {
        return "utils";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    private String getColor(double mean) {

        String hex = "#00ff00";

        if (mean >= 50) hex = "#ff0000";
        else if (mean >= 0) hex = gradient.get((int) mean);

        return ChatColor.of(hex).toString();
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {

        if (identifier.contains("load_"))
            return loadPlaceholderParse(identifier);

        switch (identifier) {
            case "mspt" -> {

                GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> mspt = spark.mspt();
                DoubleAverageInfo msptLastMin = mspt.poll(StatisticWindow.MillisPerTick.SECONDS_10);
                double min = msptLastMin.min();
                double median = msptLastMin.median();
                double mean = msptLastMin.percentile95th();
                //double max = msptLastMin.max();
                String minColor = getColor(min);
                String medianColor = getColor(median);
                String meanColor = getColor(mean);
                //String maxColor = getColor(max);
                String defaultColor = "§x§d§3§d§3§d§3";
                return minColor + String.format("%.1f", min) + defaultColor + "↓ " +
                        medianColor + String.format("%.1f", median) + defaultColor + "~ " +
                        meanColor + String.format("%.1f", mean) + defaultColor + "↑";
                //maxColor + String.format("%.1f", max) + defaultColor + "↑";
            }
            case "suffix" -> {
                String title = PlaceholderAPI.setPlaceholders(p, "%vault_suffix%");
                return title.length() > 0 ? title : "§cНет";
            }
            case "group_time" -> {
                LuckPerms api = LuckPermsProvider.get();
                User user = api.getPlayerAdapter(Player.class).getUser(p);
                List<InheritanceNode> l = user.getNodes(NodeType.INHERITANCE)
                        .stream()
                        .filter(Node::hasExpiry)
                        .filter(node -> !node.hasExpired()).toList();
                if (l.size() == 0) return "∞";
                long time = l.get(0).getExpiry().getEpochSecond() - System.currentTimeMillis() / 1000;
                if (time < 0) return "";
                if (time == 0) return "∞";
                long days = time / 86400;
                return (days != 0 ? days + "д" : "1д");
            }
        }
        return null;
    }

    /*
    tps
    cpu
    mspt
    memory
    nonheap
    gc

     */

    private String loadPlaceholderParse(String identifier) {

        String[] args = identifier.split("_");
        String sub = args[1];

        switch (sub) {

            case "tps":
                DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
                assert tps != null;

                double[] values = {
                        tps.poll(StatisticWindow.TicksPerSecond.SECONDS_5),
                        tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10),
                        tps.poll(StatisticWindow.TicksPerSecond.MINUTES_1),
                        tps.poll(StatisticWindow.TicksPerSecond.MINUTES_5),
                        tps.poll(StatisticWindow.TicksPerSecond.MINUTES_15)
                };

                return applyValues(LoadStat.TPS, values);

            case "mspt10s":
                GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> mspt = spark.mspt();
                assert mspt != null;

                values = new double[]{
                        mspt.poll(StatisticWindow.MillisPerTick.SECONDS_10).min(),
                        mspt.poll(StatisticWindow.MillisPerTick.SECONDS_10).median(),
                        mspt.poll(StatisticWindow.MillisPerTick.SECONDS_10).percentile95th(),
                        mspt.poll(StatisticWindow.MillisPerTick.SECONDS_10).max()
                };

                return applyValues(LoadStat.MSPT, values);

            case "mspt1m":
                mspt = spark.mspt();
                assert mspt != null;

                values = new double[]{
                        mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1).min(),
                        mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1).median(),
                        mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1).percentile95th(),
                        mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1).max(),
                };

                return applyValues(LoadStat.MSPT, values);

            case "gc":
                Map<String, GarbageCollector> gc = spark.gc();

                StringBuilder stringBuilder = new StringBuilder();

                gc.forEach((name, garbageCollector) -> {
                    stringBuilder
                            .append("§f").append(garbageCollector.name().substring(0, 4))
                            .append(" §f→")
                            .append(" §8avg=§7").append(Math.round(garbageCollector.avgTime())).append("ms")
                            .append(" §8freq=§7").append(String.format("%.1f", garbageCollector.avgFrequency() / 1000.0)).append("s")
                            .append("\n");
                });

                //.append(" §8totalTime=§7").append(garbageCollector.totalTime())
                //.append(" §8totalCollections=§7").append(garbageCollector.totalCollections())

                return stringBuilder.toString();

            case "ram":
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

                String r = "";
                r += addBasicMemoryStats(memoryMXBean);
                r += addDetailedMemoryStats(memoryMXBean);

                return r;
        }

        return null;
    }

    private static String addBasicMemoryStats(MemoryMXBean memoryMXBean) {

        String result = "";

        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

        result += "§7RAM (§a" + FormatUtil.percent(heapUsage.getUsed(), heapUsage.getMax()) + "§7) ";
        result += StatisticFormatter.generateMemoryUsageDiagram(heapUsage, 60);
        result += " §f" + FormatUtil.formatBytes(heapUsage.getUsed()) +
                "§7/§f" + FormatUtil.formatBytes(heapUsage.getMax());

        result += "\n";
        return result;
    }

    private static String addDetailedMemoryStats(MemoryMXBean memoryMXBean) {

        String result = "";

        MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean memoryPool : memoryPoolMXBeans) {
            if (memoryPool.getType() != MemoryType.HEAP) {
                continue;
            }

            MemoryUsage usage = memoryPool.getUsage();
            MemoryUsage collectionUsage = memoryPool.getCollectionUsage();

            if (usage.getMax() == -1) {
                usage = new MemoryUsage(usage.getInit(), usage.getUsed(), usage.getCommitted(), usage.getCommitted());
            }

            result += "§7" + memoryPool.getName() + " §7(§a" + FormatUtil.percent(usage.getUsed(), usage.getMax()) + "§7) ";
            result += StatisticFormatter.generateMemoryPoolDiagram(usage, collectionUsage, 60) + " §f" + FormatUtil.formatBytes(usage.getUsed()) + " §7/ §f" + FormatUtil.formatBytes(usage.getMax());

            if (collectionUsage != null) {
                result += " §8(last " + FormatUtil.formatBytes(collectionUsage.getUsed()) + "§8)";
            }
            result += "\n";
        }

        result += " \n§7Non-heap memory §f→ " + FormatUtil.formatBytes(nonHeapUsage.getUsed());

        return result;
    }


    private String applyValues(LoadStat loadStat, double[] values) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            double value = Math.round(values[i] * 10.0) / 10.0;

            double offset = getOffset(loadStat, value);
            offset = offset > 1 ? 1 : offset < 0 ? 0 : offset;

            int colorIndex = (int) (offset * 100);

            String hex;
            if (colorIndex >= 100) hex = "#ff0000";
            else hex = gradient.get(colorIndex);
            hex = ChatColor.of(hex).toString();


            stringBuilder.append(hex).append(String.format("%4.1f", value));

            if (loadStat == LoadStat.MSPT) {
                if (i == 0 || i == 4) stringBuilder.append("§f↓");
                if (i == 1 || i == 5) stringBuilder.append("§f~");
                if (i == 2 || i == 6) stringBuilder.append("§f≈");
                if (i == 3 || i == 7) stringBuilder.append("§f↑");
            }

            if (i != values.length - 1) stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private double getOffset(LoadStat loadStat, double value) {
        if (loadStat == LoadStat.TPS)
            return Math.abs(20.0 - value);

        if (loadStat == LoadStat.MSPT) {
            return value / 100.0;
        }

        return -1;
    }
}