package utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class InventoryUtils {

    public static ItemStack createItem(Material material, String display, List<String> lore) {
        return createItem(new ItemStack(material), display, lore);
    }

    public static ItemStack createItem(ItemStack icon, String display, List<String> lore) {
        ItemMeta iconMta = icon.getItemMeta();
        iconMta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        iconMta.setDisplayName(display);

        if (lore != null) iconMta.setLore(lore);

        icon.setItemMeta(iconMta);
        return icon;
    }

    public static ItemStack generateHead(String texture) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) i.getItemMeta();
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));
            Field profileField = sm.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(sm, profile);
        } catch (Exception e) {
        }
        i.setItemMeta(sm);
        return i;
    }

    public static <T, Z> void setData(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {

        if (key == null) return;
        if (!item.hasItemMeta()) return;
        if (item.getItemMeta() == null) return;

        ItemMeta itemMeta = item.getItemMeta();

        PersistentDataContainer dataStorage = itemMeta.getPersistentDataContainer();
        dataStorage.set(key, type, value);

        item.setItemMeta(itemMeta);
    }

    public static <T, Z> void setData(ItemStack item, String keyTitle, PersistentDataType<T, Z> type, Z value) {

        NamespacedKey key = NamespacedKey.fromString(keyTitle);
        setData(item, key, type, value);
    }


    public static <T, Z> Z getData(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> type) {

        if (key == null) return null;
        if (!item.hasItemMeta()) return null;
        if (item.getItemMeta() == null) return null;

        PersistentDataContainer dataStorage = item.getItemMeta().getPersistentDataContainer();
        if (!dataStorage.has(key, type)) return null;

        return dataStorage.get(key, type);
    }

    public static <T, Z> Z getData(ItemStack item, String keyTitle, PersistentDataType<T, Z> type) {

        NamespacedKey key = NamespacedKey.fromString(keyTitle);
        return getData(item, key, type);
    }
}
