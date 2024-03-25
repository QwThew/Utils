package utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigurationUtils {
    public static Location getLocation(Configuration configuration, String path) {

        ConfigurationSection configurationSection = configuration.getConfigurationSection(path);
        if (configurationSection == null) return null;

        return getLocation(configurationSection);
    }

    public static Location getLocation(ConfigurationSection section) {

        String worldName = section.getString("world", null);
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");

        float yaw = (float) section.getDouble("yaw", 0);
        float pitch = (float) section.getDouble("pitch", 0);

        Location location = new Location(world, x, y, z);

        location.setYaw(yaw);
        location.setPitch(pitch);

        return location;
    }
}
