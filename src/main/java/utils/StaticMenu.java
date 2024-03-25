package utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class StaticMenu implements InventoryHolder {

    private final Inventory inventory;
    private final HashMap<Integer, OnClick> onClickList = new HashMap<>();

    public StaticMenu(int size, String title) {
        inventory = Bukkit.createInventory(this, size, title);
    }

    public void setItem(int slot, ItemStack itemStack, OnClick onClick) {

        inventory.setItem(slot, itemStack);
        onClickList.put(slot, onClick);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        OnClick onClick = onClickList.getOrDefault(slot, null);
        if (onClick == null) return;

        onClick.run(event);
    }
}
