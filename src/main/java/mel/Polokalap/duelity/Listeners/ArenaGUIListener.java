package mel.Polokalap.duelity.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mel.Polokalap.duelity.GUI.*;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.AddArenaManager;
import mel.Polokalap.duelity.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ArenaGUIListener implements Listener {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    private HashMap<Player, Long> now = new HashMap<>();
    private int delay = 250;

    private ArrayList<Player> muteChat = new ArrayList<>();
    private ArrayList<Player> renamingArena = new ArrayList<>();
    private ArrayList<Player> settingArenaIcon = new ArrayList<>();


    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack item = event.getCurrentItem();

        if (item == null) return;

        if (inv.getHolder() instanceof GUI) {

            String name = item.getItemMeta().getDisplayName();

            event.setCancelled(true);

            if (now.get(player) == null) now.put(player, System.currentTimeMillis());

            if (System.currentTimeMillis() - now.get(player) < delay) return;

            now.put(player, System.currentTimeMillis());

            if (ItemUtil.PDCHelper("arenas_add_name", item)) {

                Sound.Click(player);

                AddArenaManager.join(player);
                return;

            }

            if (ItemUtil.PDCHelper("arenas_edit_edit_name_name", item)) {

                Sound.Click(player);

                renamingArena.add(player);
                player.closeInventory();

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!renamingArena.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("arenas.edit.edit_name.action_bar"));

                        if (!renamingArena.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

                return;

            }

            if (ItemUtil.PDCHelper("arenas_edit_icon_name", item)) {

                Sound.Click(player);

                settingArenaIcon.add(player);
                player.closeInventory();

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!settingArenaIcon.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("arenas.edit.icon.action_bar"));

                        if (!settingArenaIcon.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

                return;

            }

            if (ItemUtil.PDCHelper("arenas_edit_delete_name", item)) {

                Sound.Click(player);

                ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");
                if (arenas == null) return;

                String targetName = PlayerCache.editArenaName.get(player);

                List<Integer> ids = arenas.getKeys(false).stream()
                        .map(Integer::parseInt)
                        .sorted()
                        .toList();

                boolean shift = false;

                for (int id : ids) {

                    String key = String.valueOf(id);
                    ConfigurationSection arena = arenas.getConfigurationSection(key);
                    if (arena == null) continue;

                    if (arena.getString("name").equals(targetName)) {

                        arenas.set(key, null);
                        shift = true;
                        continue;

                    }

                    if (shift) {

                        String newKey = String.valueOf(id - 1);

                        ConfigurationSection target = arenas.createSection(newKey);

                        for (String subKey : arena.getKeys(false)) {

                            target.set(subKey, arena.get(subKey));

                        }

                        arenas.set(key, null);

                    }

                }

                plugin.saveArenaConfig();

                File file = new File(plugin.getDataFolder(), "Maps/" + PlayerCache.editArenaName.get(player) + ".schem");

                if (file.delete()) {

                    player.sendMessage(NewConfig.getString("arenas.edit.delete.deleted"));

                } else {

                    player.sendMessage(NewConfig.getString("arenas.edit.delete.failed_to_delete"));

                }

                new ArenaManagerGUI().openGUI(player);

                return;

            }

            if (ItemUtil.PDCHelper("arena_back_button", item)) {

                new ArenaManagerGUI().openGUI(player);

            }

            if (!event.getView().getTitle().equals(NewConfig.getString("arenas.name"))) return;

            ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

            for (String arenaId : arenas.getKeys(false)) {

                ConfigurationSection arena = arenas.getConfigurationSection(arenaId);

                if (ItemUtil.PDCHelper("arena-" + arena.get("name"), item)) {

                    if (!event.isRightClick()) return;

                    Sound.Click(player);
                    PlayerCache.editArenaName.put(player, name.replaceAll(NewConfig.getString("arenas.arena.color"), ""));
                    new EditArenaGUI().openGUI(player);

                }

            }

        }

    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {

        Player player = event.getPlayer();
        String message = event.signedMessage().message();

        if (renamingArena.contains(player)) {

            event.setCancelled(true);

            if (!message.isEmpty()) {

                Bukkit.getScheduler().runTask(plugin, () -> {

                    ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

                    List<Integer> ids = arenas.getKeys(false).stream()
                            .map(Integer::parseInt)
                            .sorted()
                            .toList();

                    String targetName = PlayerCache.editArenaName.get(player);

                    for (int id : ids) {

                        String key = String.valueOf(id);
                        ConfigurationSection arena = arenas.getConfigurationSection(key);
                        if (arena == null) continue;

                        if (arena.getString("name").equals(targetName)) {

                            boolean exists = false;

                            for (int id1 : ids) {

                                String key1 = String.valueOf(id1);
                                ConfigurationSection arena1 = arenas.getConfigurationSection(key1);
                                if (arena1 == null) continue;

                                if (arena1.getString("name").equals(message)) exists = true;

                            }

                            if (exists) {

                                player.sendMessage(NewConfig.getString("arenas.edit.edit_name.exists"));
                                Sound.Error(player);

                            } else {

                                File from = new File(plugin.getDataFolder(), "Maps/" + PlayerCache.editArenaName.get(player) + ".schem");
                                File to = new File(plugin.getDataFolder(), "Maps/" + message + ".schem");

                                try {
                                    Files.move(Path.of(from.getPath()), Path.of(to.getPath()));
                                } catch (IOException ignored) {
                                }

                                arenas.set(key + ".name", message);
                                PlayerCache.editArenaName.put(player, message);

                                player.sendActionBar(" ");

                                plugin.saveArenaConfig();

                                new EditArenaGUI().openGUI(player);

                                renamingArena.remove(player);
                                muteChat.remove(player);

                            }

                        }

                    }

                });

            }

        }

        if (settingArenaIcon.contains(player)) {

            event.setCancelled(true);

            if (message.equals("done")) {

                settingArenaIcon.remove(player);
                muteChat.remove(player);

                Bukkit.getScheduler().runTask(plugin, () -> {

                    player.sendActionBar(" ");

                    ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

                    List<Integer> ids = arenas.getKeys(false).stream()
                            .map(Integer::parseInt)
                            .sorted()
                            .toList();

                    String targetName = PlayerCache.editArenaName.get(player);

                    for (int id : ids) {

                        String key = String.valueOf(id);
                        ConfigurationSection arena = arenas.getConfigurationSection(key);
                        if (arena == null) continue;

                        if (arena.getString("name").equals(targetName)) {

                            arenas.set(key + ".icon", player.getInventory().getItemInMainHand().getType().toString());

                        }

                    }

                    new EditArenaGUI().openGUI(player);

                    plugin.saveArenaConfig();

                });

            }

        }

    }

    @EventHandler
    public void onChatMessageSent(AsyncPlayerChatEvent event) {

        event.getRecipients().removeIf(player -> muteChat.contains(player));

    }

}
