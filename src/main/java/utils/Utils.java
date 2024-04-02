package utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class Utils extends JavaPlugin implements CommandExecutor, Listener {

    private List<String> bannedNameRegexes = new ArrayList<>();
    private List<String> bannedIpRegexes = new ArrayList<>();

    @Override
    public void onEnable() {
        getCommand("banipmask").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);

        updateBlackLists();

        new PlaceHolder().register();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof StaticMenu staticMenu) {
            event.setCancelled(true);

            if (event.getClickedInventory() == staticMenu.getInventory())
                staticMenu.onClick(event);
        }
    }

    private void updateBlackLists() {

        File file = new File(getDataFolder(), "blacklists.yml");

        if (!file.exists()) {
            Bukkit.getLogger().log(Level.WARNING, "Файл blacklists.yml не найден!");
            return;
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        bannedIpRegexes = yamlConfiguration.getStringList("ip_patterns");
        bannedNameRegexes = yamlConfiguration.getStringList("name_patterns");
    }

    @EventHandler
    public void onPreJoin(PlayerLoginEvent event) {

        String playerName = event.getPlayer().getName();
        String ipString = event.getAddress().toString().substring(1);

        if (bannedNameRegexes.stream().anyMatch(playerName::matches) || bannedIpRegexes.stream().anyMatch(ipString::matches)) {

            Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + " " + playerName + " BLOCKED by Utils plugin");
            event.setKickMessage("");
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equals("reload")) {
            updateBlackLists();
            sender.sendMessage(" Конфигурация обновлена");
            return true;
        }

        if (args.length == 2 && args[0].equals("addip")) {

            bannedIpRegexes.add(args[1]);
            sender.sendMessage(" Конфигурация обновлена");
            save();
            return true;
        }

        if (args.length == 2 && args[0].equals("addname")) {

            bannedNameRegexes.add(args[1]);
            sender.sendMessage(" Конфигурация обновлена");
            save();
            return true;
        }


        String regex = args[0];
        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) reason.append(args[i]).append(" ");

        for (String player : getPlayersFromRegex(regex)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:ban-ip " + player + " " + reason);
        }
        return true;
    }

    private void save() {
        File file = new File(getDataFolder(), "blacklists.yml");

        if (!file.exists()) {
            Bukkit.getLogger().log(Level.WARNING, "Файл blacklists.yml не найден!");
            return;
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("ip_patterns", bannedIpRegexes);
        yamlConfiguration.set("name_patterns", bannedNameRegexes);

        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getPlayersFromRegex(String regex) {
        List<String> players = new ArrayList<>();
        try {

            File file = new File("database.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
            Connection connection = DriverManager.getConnection("jdbc:mysql://" +
                            cfg.getString("host") + ":" +
                            cfg.getString("port") + "/authme",
                    cfg.getString("user"),
                    cfg.getString("password"));
            Statement st = connection.createStatement();
            st.execute("SELECT realname FROM authme WHERE realname regexp " + regex + ";");
            ResultSet r = st.getResultSet();
            while (r.next()) players.add(r.getString("realname"));
            r.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
