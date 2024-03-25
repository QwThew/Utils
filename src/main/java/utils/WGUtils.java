package utils;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;

public class WGUtils {
    public static boolean hasRegion(org.bukkit.Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());
        return container.createQuery().getApplicableRegions(new Location(bukkitWorld, location.getX(), location.getY(), location.getZ())).size() > 0;
    }

    public static ProtectedRegion[] getRegions(World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        BukkitWorld bukkitWorld = new BukkitWorld(world);

        RegionManager regionManager = container.get(bukkitWorld);
        if (regionManager == null) return null;

        return regionManager.getRegions().values().toArray(new ProtectedRegion[0]);
    }
}
