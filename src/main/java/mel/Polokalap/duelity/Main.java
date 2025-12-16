package mel.Polokalap.duelity;

import mel.Polokalap.duelity.Commands.*;
import mel.Polokalap.duelity.Listeners.*;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.WorldUtil;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    private static Main instance;
    private File player;
    private File kit;
    private File arena;
    private FileConfiguration playersConfig;
    private FileConfiguration kitConfig;
    private FileConfiguration arenaConfig;

    @Override
    public void onEnable() {

        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        player = new File(getDataFolder(), "data/players.yml");
        kit = new File(getDataFolder(), "data/kits.yml");
        arena = new File(getDataFolder(), "data/arena.yml");

        if (!player.exists()) saveResource("data/players.yml", false);
        if (!kit.exists()) saveResource("data/kits.yml", false);
        if (!arena.exists()) saveResource("data/arena.yml", false);

        playersConfig = YamlConfiguration.loadConfiguration(player);
        kitConfig = YamlConfiguration.loadConfiguration(kit);
        arenaConfig = YamlConfiguration.loadConfiguration(arena);

        register_stuff();

        getLogger().info(getConfig().getString("console.startup"));

        new WorldUtil().makeEmptyWorld("arenas", false);

    }


    private void register_stuff() {

        // Command: getCommand("command").setExecutor(new Class());
        getCommand("#setup").setExecutor(new SetupCommand());
        getCommand("#kits").setExecutor(new KitsCommand());
        getCommand("#wetest").setExecutor(new WorldEditTestCommand());
        getCommand("#arenas").setExecutor(new ArenasAddCommand());

        // Listener: getServer().getPluginManager().registerEvents(new Class(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new ArenaGUIListener(), this);

        // getCommand("command").setTabCompleter(new Class());
        getCommand("#setup").setTabCompleter(new SetupCommand());
        getCommand("#kits").setTabCompleter(new KitsCommand());
        getCommand("#wetest").setTabCompleter(new WorldEditTestCommand());
        getCommand("#arenas").setTabCompleter(new ArenasAddCommand());

    }

    @Override
    public void onDisable() {

        getLogger().info(getConfig().getString("console.shutdown"));

        for (World world : PlayerCache.worlds) {

            WorldUtil.deleteWorldByWorld(world);

        }

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

    public FileConfiguration getArenaConfig() {

        return arenaConfig;

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

    public void saveArenaConfig() {

        try {
            arenaConfig.save(arena);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}