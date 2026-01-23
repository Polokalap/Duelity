package mel.Polokalap.duelity.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mel.Polokalap.duelity.GUI.*;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.*;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.*;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUIListener implements Listener {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();
    private static FileConfiguration kits = plugin.getKitConfig();

    private HashMap<Player, Long> now = new HashMap<>();
    private int delay = 250;

    public static ArrayList<Player> muteChat = new ArrayList<>();
    private ArrayList<Player> settingSpawn = new ArrayList<>();
    public static ArrayList<Player> addingKit = new ArrayList<>();
    private ArrayList<Player> addingKitName = new ArrayList<>();
    private ArrayList<Player> addingKitIcon = new ArrayList<>();
    private ArrayList<Player> settingKitName = new ArrayList<>();
    private ArrayList<Player> settingKitIcon = new ArrayList<>();

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (inv.getHolder() instanceof GUI) {

            Sound.Close(player);

        }

    }

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

            if (ItemUtil.PDCHelper("settings_menu_duel_name", item)) {

                Sound.Click(player);

                config.set("settings.duel_server", !config.getBoolean("settings.duel_server"));
                plugin.saveConfig();

                item.setLore(List.of(NewConfig.getStringCompiled("settings_menu.duel.lore").replaceAll("ẞanswer", config.getBoolean("settings.duel_server") ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

            }

            if (ItemUtil.PDCHelper("settings_menu_skip_name", item)) {

                Sound.Click(player);

                config.set("settings.skip", !config.getBoolean("settings.skip"));
                plugin.saveConfig();

                item.setLore(List.of(NewConfig.getStringCompiled("settings_menu.skip.lore").replaceAll("ẞanswer", config.getBoolean("settings.skip") ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

            }

            if (ItemUtil.PDCHelper("settings_menu_spawn_name", item)) {

                Sound.Click(player);
                player.closeInventory();

                muteChat.add(player);
                settingSpawn.add(player);

                player.sendMessage(NewConfig.getString("settings_menu.spawn.close"));

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!settingSpawn.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("settings_menu.spawn.action_bar"));

                        if (!settingSpawn.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

            }

            if (ItemUtil.PDCHelper("kits_add_name", item)) {

                PlayerCache.tempName.remove(player);
                PlayerCache.tempIcon.remove(player);
                PlayerCache.tempKit.remove(player);
                PlayerCache.selectedArenas.remove(player);
                PlayerCache.editingKit.remove(player);
                GUIListener.addingKit.remove(player);

                PlayerCache.tempHp.put(player, 20);
                PlayerCache.tempGamemode.put(player, Gamemodes.SURVIVAL);
                PlayerCache.tempRegen.put(player, true);

                new AddKitGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("kits_add_gui_set_name_name", item)) {

                player.closeInventory();
                addingKitName.add(player);
                muteChat.add(player);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!addingKitName.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("kits.add_gui.set_name.gui.action_bar"));

                        if (!addingKitName.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

            }

            if (ItemUtil.PDCHelper("settings_menu_difficulty_name", item)) {

                Sound.Click(player);

                if (PlayerCache.tempDifficulty.get(player) == Difficulty.HARD) PlayerCache.tempDifficulty.put(player, Difficulty.EASY);
                else if (PlayerCache.tempDifficulty.get(player) == Difficulty.EASY) PlayerCache.tempDifficulty.put(player, Difficulty.NORMAL);
                else PlayerCache.tempDifficulty.put(player, Difficulty.HARD);

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setLore(List.of(
                        NewConfig.getStringList("settings_menu.difficulties").get(0).replaceAll("ẞa", PlayerCache.tempDifficulty.get(player) == Difficulty.EASY ? "§a§u" : "§7"),
                        NewConfig.getStringList("settings_menu.difficulties").get(1).replaceAll("ẞb", PlayerCache.tempDifficulty.get(player) == Difficulty.NORMAL ? "§a§u" : "§7"),
                        NewConfig.getStringList("settings_menu.difficulties").get(2).replaceAll("ẞc", PlayerCache.tempDifficulty.get(player) == Difficulty.HARD ? "§a§u" : "§7")
                ));

                item.setItemMeta(itemMeta);

                config.set("settings.difficulty", PlayerCache.tempDifficulty.get(player).toString());
                plugin.saveConfig();

                for (World world : PlayerCache.worlds) {

                    world.setDifficulty(Difficulty.valueOf(config.getString("settings.difficulty")));

                }

            }

            if (ItemUtil.PDCHelper("kits_add_gui_set_icon_name", item)) {

                player.closeInventory();
                addingKitIcon.add(player);
                muteChat.add(player);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!addingKitIcon.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("kits.add_gui.set_icon.action_bar"));

                        if (!addingKitIcon.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

            }

            if (ItemUtil.PDCHelper("kits_add_gui_set_inventory_name", item)) {

                player.closeInventory();
                addingKit.add(player);
                muteChat.add(player);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!addingKit.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("kits.add_gui.set_inventory.action_bar"));

                        if (!addingKit.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

            }

            if (ItemUtil.PDCHelper("kits_add_gui_regen_name", item)) {

                Sound.Click(player);

                PlayerCache.tempRegen.put(player, !PlayerCache.tempRegen.get(player));

                item.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.regen.lore").replaceAll("ẞanswer", PlayerCache.tempRegen.get(player) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

                if (PlayerCache.tempRegen.get(player)) item.setType(Material.COOKED_BEEF);
                else item.setType(Material.ROTTEN_FLESH);

            }

            if (ItemUtil.PDCHelper("kits_add_gui_next_health_name", item)) {

                if (event.isRightClick()) {

                    if (PlayerCache.tempHp.get(player) <= 1 || event.isShiftClick() && PlayerCache.tempHp.get(player) <= 10) {

                        player.sendMessage(NewConfig.getString("kits.add_gui.health.max"));
                        Sound.Error(player);
                        return;

                    }

                    if (event.isShiftClick()) PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) - 10);
                    else PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) - 1);
                    Sound.Drink(player, true);

                } else if (event.isLeftClick()) {

                    if (event.isShiftClick()) PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) + 10);
                    else PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) + 1);
                    Sound.Drink(player, false);

                }

                String itemName = NewConfig.getString("kits.add_gui.health.name").replaceAll("ẞhp", String.valueOf(PlayerCache.tempHp.get(player)));

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setDisplayName(itemName);

                item.setItemMeta(itemMeta);

            }

            if (ItemUtil.PDCHelper("admin_edit_kit_gui_health", item)) {

                if (event.isRightClick()) {

                    if (PlayerCache.tempHp.get(player) <= 1 || event.isShiftClick() && PlayerCache.tempHp.get(player) <= 10) {

                        player.sendMessage(NewConfig.getString("editor.edit.health.max"));
                        Sound.Error(player);
                        return;

                    }

                    if (event.isShiftClick()) PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) - 10);
                    else PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) - 1);
                    Sound.Drink(player, true);

                } else if (event.isLeftClick()) {

                    if (event.isShiftClick()) PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) + 10);
                    else PlayerCache.tempHp.put(player, PlayerCache.tempHp.get(player) + 1);
                    Sound.Drink(player, false);

                }

                String itemName = NewConfig.getString("editor.edit.health.name").replaceAll("ẞhp", String.valueOf(PlayerCache.tempHp.get(player)));

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setDisplayName(itemName);

                item.setItemMeta(itemMeta);

                ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

                kit.set("health", PlayerCache.tempHp.get(player));

                plugin.saveKitConfig();

            }

            if (ItemUtil.PDCHelper("kits_add_gui_next_gamemode_name", item)) {

                Sound.Click(player);

                if (PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL) PlayerCache.tempGamemode.put(player, Gamemodes.ADVENTURE);
                else PlayerCache.tempGamemode.put(player, Gamemodes.SURVIVAL);

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setLore(List.of(
                        NewConfig.getStringList("kits.add_gui.gamemode.lore").get(0).replaceAll("ẞa", PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL ? "§a§u" : "§7"),
                        NewConfig.getStringList("kits.add_gui.gamemode.lore").get(1).replaceAll("ẞb", PlayerCache.tempGamemode.get(player) == Gamemodes.ADVENTURE ? "§a§u" : "§7")
                ));

                item.setItemMeta(itemMeta);

            }

            if (ItemUtil.PDCHelper("kits_add_gui_next_map_name", item)) {

                new AddKitSelectArenaGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("kits_save_name", item)) {

                boolean exists = false;
                ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

                if (kitsSection == null) {
                    kitsSection = kits.createSection("kits");
                }

                for (String key : kitsSection.getKeys(false)) {

                    ConfigurationSection kit = kitsSection.getConfigurationSection(key);
                    if (kit == null) continue;

                    String kitName = kit.getString("name");
                    if (kitName == null) continue;

                    if (kitName.equals(PlayerCache.tempName.get(player))) {
                        exists = true;
                    }

                }

                if (exists) {

                    Sound.Error(player);
                    player.sendMessage(NewConfig.getString("kits.add_gui.set_name.gui.exists"));
                    return;

                }

                int number = kitsSection.getKeys(false).size();

                if (number >= 28) {

                    Sound.Error(player);
                    player.sendMessage(NewConfig.getString("kits.too_many"));
                    return;

                }

                ConfigurationSection kit = kitsSection.createSection(String.valueOf(number));

                kit.set("name", PlayerCache.tempName.get(player));
                kit.set("icon", PlayerCache.tempIcon.get(player).toString());
                kit.set("items", PlayerCache.tempKit.get(player));
                kit.set("health", PlayerCache.tempHp.get(player));
                kit.set("gamemode", PlayerCache.tempGamemode.get(player).toString());
                kit.set("regen", PlayerCache.tempRegen.get(player));
                kit.set("arenas", PlayerCache.selectedArenas.get(player));

                plugin.saveKitConfig();
                player.sendMessage(NewConfig.getString("kits.saved"));
                new KitsManagerGUI().openGUI(player);

            }

            if (event.getView().getTitle().equals(NewConfig.getString("kits.add_gui.map.name"))) {

                ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

                for (String arenaId : arenas.getKeys(false)) {

                    ConfigurationSection arena = arenas.getConfigurationSection(arenaId);

                    if (ItemUtil.PDCHelper("arena-" + arena.get("name"), item)) {

                        Sound.Click(player);

                        String editedName = name.replaceAll(NewConfig.getString("arenas.arena.color"), "");

                        ItemMeta meta = item.getItemMeta();

                        if (!PlayerCache.selectedArenas.containsKey(player)) PlayerCache.selectedArenas.put(player, new ArrayList<>());

                        if (PlayerCache.selectedArenas.get(player).contains(editedName)) PlayerCache.selectedArenas.get(player).remove(editedName);
                        else PlayerCache.selectedArenas.get(player).add(editedName);

                        meta.setEnchantmentGlintOverride(PlayerCache.selectedArenas.get(player).contains(editedName));

                        item.setItemMeta(meta);

                    } else if (ItemUtil.PDCHelper("arena-" + arena.get("name") + "-editkit", item)) {

                        Sound.Click(player);

                        String editedName = name.replaceAll(NewConfig.getString("arenas.arena.color"), "");

                        ItemMeta meta = item.getItemMeta();

                        if (!PlayerCache.selectedArenas.containsKey(player)) PlayerCache.selectedArenas.put(player, new ArrayList<>());

                        if (PlayerCache.selectedArenas.get(player).contains(editedName)) PlayerCache.selectedArenas.get(player).remove(editedName);
                        else PlayerCache.selectedArenas.get(player).add(editedName);

                        meta.setEnchantmentGlintOverride(PlayerCache.selectedArenas.get(player).contains(editedName));

                        item.setItemMeta(meta);

                        ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

                        kit.set("arenas", PlayerCache.selectedArenas.get(player));

                        plugin.saveKitConfig();

                    }

                }

            }

            if (ItemUtil.PDCHelper("admin_kit_editor_attributes_back", item)) {

                new KitManagerEditGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("select_arena_add_kit_back_button", item)) {

                new AddKitGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("editor_edit_map_name", item)) {

                new EditKitSelectArenaGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("edit_kit_select_arena_back", item)) {

                new AdminEditKitGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("admin_edit_kit_gui_gamemode", item)) {

                Sound.Click(player);

                if (PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL) PlayerCache.tempGamemode.put(player, Gamemodes.ADVENTURE);
                else PlayerCache.tempGamemode.put(player, Gamemodes.SURVIVAL);

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setLore(List.of(
                        NewConfig.getStringList("editor.edit.gamemode.lore").get(0).replaceAll("ẞa", PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL ? "§a§u" : "§7"),
                        NewConfig.getStringList("editor.edit.gamemode.lore").get(1).replaceAll("ẞb", PlayerCache.tempGamemode.get(player) == Gamemodes.ADVENTURE ? "§a§u" : "§7")
                ));

                item.setItemMeta(itemMeta);

                ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

                kit.set("gamemode", PlayerCache.tempGamemode.get(player).toString());

                plugin.saveKitConfig();

            }

            if (ItemUtil.PDCHelper("editor_edit_delete_name", item)) {

                Sound.Ping(player);

                ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

                ConfigurationSection kitsSetcion = kits.getConfigurationSection("kits");

                if (kitsSetcion == null) return;

                String targetName = PlayerCache.editingKit.get(player);

                List<Integer> ids = kitsSetcion.getKeys(false).stream()
                        .map(Integer::parseInt)
                        .sorted()
                        .toList();

                boolean shift = false;

                for (int id : ids) {

                    String key = String.valueOf(id);
                    ConfigurationSection selectedKit = kitsSetcion.getConfigurationSection(key);
                    if (selectedKit == null) continue;

                    if (selectedKit.getString("name").equals(targetName)) {

                        kitsSetcion.set(key, null);
                        shift = true;
                        continue;

                    }

                    if (shift) {

                        String newKey = String.valueOf(id - 1);

                        ConfigurationSection target = kitsSetcion.createSection(newKey);

                        for (String subKey : selectedKit.getKeys(false)) {

                            target.set(subKey, selectedKit.get(subKey));

                        }

                        kitsSetcion.set(key, null);

                    }

                }

                player.sendMessage(NewConfig.getString("editor.edit.delete.deleted"));

                plugin.saveKitConfig();

                new KitsManagerGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("editor_edit_edit_name_name", item)) {

                player.closeInventory();
                settingKitName.add(player);
                muteChat.add(player);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!settingKitName.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("kits.add_gui.set_name.action_bar"));

                        if (!settingKitName.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

            }

            if (ItemUtil.PDCHelper("editor_edit_edit_icon_name", item)) {

                player.closeInventory();
                settingKitIcon.add(player);
                muteChat.add(player);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!settingKitIcon.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("kits.add_gui.set_icon.action_bar"));

                        if (!settingKitIcon.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

            }

            if (event.getView().getTitle().equals(NewConfig.getString("kits.name"))) {

                ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

                for (String kitId : kitsSection.getKeys(false)) {

                    ConfigurationSection kit = kitsSection.getConfigurationSection(kitId);

                    if (ItemUtil.PDCHelper("kit-" + kit.get("name"), item)) {

                        Sound.Click(player);

                        if (event.isRightClick()) {

                            PlayerCache.editingKit.put(player, (String) kit.get("name"));
                            PlayerCache.tempHp.put(player, kit.getInt("health"));
                            PlayerCache.tempRegen.put(player, kit.getBoolean("regen"));
                            PlayerCache.inKitEditor.add(player);
                            new KitManagerEditGUI().openGUI(player);

                        }

                        if (event.isLeftClick()) {

                            KitUtil.claimKit((String) kit.get("name"), player);

                        }

                    }

                }

            }

        }

    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {

        Player player = event.getPlayer();
        String message = event.signedMessage().message();

        if (settingSpawn.contains(player)) {

            event.setCancelled(true);

            if (message.equals("done")) {

                settingSpawn.remove(player);
                muteChat.remove(player);

                Bukkit.getScheduler().runTask(plugin, () -> {

                    Location loc = player.getLocation();

                    player.sendActionBar(" ");
                    new SettingsGUI().openGUI(player);

                    config.set("settings.spawn", loc);
                    plugin.saveConfig();

                });

                return;

            }

            Sound.Error(player);
            player.sendActionBar("§c" + NewConfig.getString("settings_menu.spawn.action_bar"));

        }

        if (addingKitName.contains(player)) {

            event.setCancelled(true);

            if (!message.matches("^[a-zA-Z0-9:_-]+$")) {

                Sound.Error(player);
                return;

            }

            boolean exists = false;

            ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

            if (kitsSection == null) {
                kitsSection = kits.createSection("kits");
            }

            for (String kitId : kitsSection.getKeys(false)) {

                ConfigurationSection kit = kitsSection.getConfigurationSection(kitId);

                String name = kit.getString("name");

                if (name.equals(message)) exists = true;

            }

            if (exists) {

                Sound.Error(player);
                player.sendActionBar(NewConfig.getString("kits.add_gui.set_name.gui.exists"));
                return;

            }

            addingKitName.remove(player);
            muteChat.remove(player);

            PlayerCache.tempName.put(player, message);

            Bukkit.getScheduler().runTask(plugin, () -> {

                new AddKitGUI().openGUI(player);

            });

            return;

        }

        if (addingKitIcon.contains(player)) {

            event.setCancelled(true);

            if (message.equals("done")) {

                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {

                    Sound.Error(player);
                    return;

                }

                addingKitIcon.remove(player);
                muteChat.remove(player);

                Bukkit.getScheduler().runTask(plugin, () -> {

                    player.sendActionBar(" ");

                    PlayerCache.tempIcon.put(player, player.getInventory().getItemInMainHand().getType());

                    new AddKitGUI().openGUI(player);

                });

                return;

            }

            Sound.Error(player);
            player.sendActionBar("§c" + NewConfig.getString("kits.add_gui.set_icon.action_bar"));

        }

        if (addingKit.contains(player)) {

            event.setCancelled(true);

            if (message.equals("done")) {

                addingKit.remove(player);
                muteChat.remove(player);

                Bukkit.getScheduler().runTask(plugin, () -> {

                    player.sendActionBar(" ");

                    ArrayList<ItemStack> items = new ArrayList<>();

                    for (ItemStack item : player.getInventory()) {

                        if (item != null) items.add(item);
                        else items.add(new ItemStack(Material.AIR));

                    }

                    PlayerCache.tempKit.put(player, items);

                    new AddKitGUI().openGUI(player);

                });

                return;

            }

            Sound.Error(player);
            player.sendActionBar("§c" + NewConfig.getString("kits.add_gui.set_inventory.action_bar"));

        }

        if (settingKitName.contains(player)) {

            event.setCancelled(true);

            if (!message.matches("^[a-zA-Z0-9:_-]+$")) {

                Sound.Error(player);
                return;

            }

            boolean exists = false;

            ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

            if (kitsSection == null) {
                kitsSection = kits.createSection("kits");
            }

            for (String kitId : kitsSection.getKeys(false)) {

                ConfigurationSection kit = kitsSection.getConfigurationSection(kitId);

                String name = kit.getString("name");

                if (name.equals(message)) exists = true;

            }

            if (exists) {

                Sound.Error(player);
                player.sendActionBar(NewConfig.getString("kits.add_gui.set_name.gui.exists"));
                return;

            }

            settingKitName.remove(player);
            muteChat.remove(player);

            ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

            kit.set("name", message);

            PlayerCache.editingKit.put(player, message);

            plugin.saveKitConfig();

            Bukkit.getScheduler().runTask(plugin, () -> {

                new AdminEditKitGUI().openGUI(player);

            });

            return;

        }

        if (settingKitIcon.contains(player)) {

            event.setCancelled(true);

            if (message.equals("done")) {

                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {

                    Sound.Error(player);
                    return;

                }

                settingKitIcon.remove(player);
                muteChat.remove(player);

                Bukkit.getScheduler().runTask(plugin, () -> {

                    player.sendActionBar(" ");

                    new AdminEditKitGUI().openGUI(player);

                });

                ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));
                kit.set("icon", player.getInventory().getItemInMainHand().getType().toString());
                plugin.saveKitConfig();

                return;

            }

            Sound.Error(player);
            player.sendActionBar("§c" + NewConfig.getString("kits.add_gui.set_icon.action_bar"));

        }

    }

    @EventHandler
    public void onChatMessageSent(AsyncPlayerChatEvent event) {

        event.getRecipients().removeIf(player -> muteChat.contains(player));

    }

}
