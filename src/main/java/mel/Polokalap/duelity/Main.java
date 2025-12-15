package mel.Polokalap.duelity;

import mel.Polokalap.duelity.Commands.*;
import mel.Polokalap.duelity.Listeners.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    private static Main instance;
    private File player;
    private File kit;
    private FileConfiguration playersConfig;
    private FileConfiguration kitConfig;

    @Override
    public void onEnable() {

        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        player = new File(getDataFolder(), "data/players.yml");
        kit = new File(getDataFolder(), "data/kits.yml");

        if (!player.exists()) saveResource("data/players.yml", false);
        if (!kit.exists()) saveResource("data/kits.yml", false);

        playersConfig = YamlConfiguration.loadConfiguration(player);
        kitConfig = YamlConfiguration.loadConfiguration(kit);

        register_stuff();

        getLogger().info(getConfig().getString("console.startup"));

    }


    private void register_stuff() {

        // Command: getCommand("command").setExecutor(new Class());
        getCommand("#setup").setExecutor(new SetupCommand());
        getCommand("#kits").setExecutor(new KitsCommand());

        // Listener: getServer().getPluginManager().registerEvents(new Class(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        // getCommand("command").setTabCompleter(new Class());
        getCommand("#setup").setTabCompleter(new SetupCommand());
        getCommand("#kits").setTabCompleter(new KitsCommand());

    }

    @Override
    public void onDisable() {

        getLogger().info(getConfig().getString("console.shutdown"));

    }

    public static Main getInstance() {

        return instance;

    }


    public FileConfiguration getPlayerConfig() {

        return playersConfig;

    }

    public FileConfiguration getKitConfig() {

        return kitConfig;

    }

    public void savePlayerConfig() {

        try {
            playersConfig.save(player);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void saveKitConfig() {

        try {
            kitConfig.save(kit);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}