package mel.Polokalap.duelity;

import mel.Polokalap.duelity.Commands.*;
import mel.Polokalap.duelity.Listeners.*;
import mel.Polokalap.duelity.Managers.AddArenaManager;
import mel.Polokalap.duelity.Managers.DuelManager;
import mel.Polokalap.duelity.Managers.KitEditorManager;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.WorldUtil;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

        ConfigurationSection arenas = getArenaConfig().getConfigurationSection("arenas");

        for (String arenaId : arenas.getKeys(false)) {

            ConfigurationSection arena = arenas.getConfigurationSection(arenaId);
            PlayerCache.arenaNames.clear();
            PlayerCache.arenaNames.add(arena.getString("name"));

        }

        register_stuff();

        getLogger().info(getConfig().getString("console.startup"));

        new WorldUtil().makeEmptyWorld("arenas", false);

    }


    private void register_stuff() {

        // Command: getCommand("command").setExecutor(new Class());
        getCommand("#setup").setExecutor(new SetupCommand());
        getCommand("#kits").setExecutor(new KitsCommand());
//        getCommand("#wetest").setExecutor(new WorldEditTestCommand());
        getCommand("#arenas").setExecutor(new ArenasCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
        getCommand("editkit").setExecutor(new EditKitCommand());
        getCommand("duel").setExecutor(new DuelCommand());
        getCommand("cancelduel").setExecutor(new CancelDuelCommand());
        getCommand("declineduel").setExecutor(new DeclineDuelCommand());
        getCommand("acceptduel").setExecutor(new AcceptDuelCommand());
        getCommand("spectate").setExecutor(new SpectateCommand());

        // Listener: getServer().getPluginManager().registerEvents(new Class(), this);
        getServer().getPluginManager().registerEvents(new PlayerStateListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new ArenaGUIListener(), this);
        getServer().getPluginManager().registerEvents(new AddArenaListener(), this);
        getServer().getPluginManager().registerEvents(new EditKitGUIListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKitEditorGUIListener(), this);
        getServer().getPluginManager().registerEvents(new DuelListener(), this);

        // getCommand("command").setTabCompleter(new Class());
        getCommand("#setup").setTabCompleter(new SetupCommand());
        getCommand("#kits").setTabCompleter(new KitsCommand());
//        getCommand("#wetest").setTabCompleter(new WorldEditTestCommand());
        getCommand("#arenas").setTabCompleter(new ArenasCommand());
        getCommand("leave").setTabCompleter(new LeaveCommand());
        getCommand("editkit").setTabCompleter(new EditKitCommand());
        getCommand("duel").setTabCompleter(new DuelCommand());
        getCommand("cancelduel").setTabCompleter(new CancelDuelCommand());
        getCommand("declineduel").setTabCompleter(new DeclineDuelCommand());
        getCommand("acceptduel").setTabCompleter(new AcceptDuelCommand());
        getCommand("spectate").setTabCompleter(new SpectateCommand());

    }

    @Override
    public void onDisable() {

        getLogger().info(getConfig().getString("console.shutdown"));

        for (Player player : PlayerCache.inDuel) {

            player.getInventory().clear();
            player.teleport(PlayerCache.duelPreLocation.get(player));
            player.setGameMode(PlayerCache.duelPreGameMode.get(player));
            player.setInvulnerable(false);
            player.setMaxHealth(20.0d);
            ItemStack[] contents = PlayerCache.playerInventory.get(player);

            if (contents != null) {

                player.getInventory().setContents(contents);

            }

        }

        for (World world : PlayerCache.worlds) {

            WorldUtil.deleteWorldByWorld(world);

        }

        for (Player player : PlayerCache.editingArena) {

            AddArenaManager.leave(player);

        }

        for (Player player : PlayerCache.inPlayerKitEditor) {

            KitEditorManager.leave(player, player.getOpenInventory().getTopInventory(), false);

        }

        for (Player player : PlayerCache.spectating) {

            player.teleport(PlayerCache.spectatePreLocation.get(player));
            player.setGameMode(PlayerCache.spectatePreGameMode.get(player));

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