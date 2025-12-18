package mel.Polokalap.duelity.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mel.Polokalap.duelity.GUI.AddKitAttributesGUI;
import mel.Polokalap.duelity.GUI.AddKitGUI;
import mel.Polokalap.duelity.GUI.GUI;
import mel.Polokalap.duelity.GUI.SetupGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import java.util.Arrays;
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
    private ArrayList<Player> addingKit = new ArrayList<>();
    private ArrayList<Player> addingKitName = new ArrayList<>();
    private ArrayList<Player> addingKitIcon = new ArrayList<>();

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

            if (name.equals(NewConfig.getString("setup.duel.name"))) {

                Sound.Click(player);

                config.set("settings.duel_server", !config.getBoolean("settings.duel_server"));
                plugin.saveConfig();

                item.setLore(List.of(NewConfig.getStringCompiled("setup.duel.lore").replaceAll("ẞanswer", config.getBoolean("settings.duel_server") ? NewConfig.getString("player.y") : NewConfig.getString("player.n"))));

            }

            if (name.equals(NewConfig.getString("setup.spawn.name"))) {

                Sound.Click(player);
                player.closeInventory();

                muteChat.add(player);
                settingSpawn.add(player);

                player.sendMessage(NewConfig.getString("setup.spawn.close"));

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!settingSpawn.contains(player)) cancel();

                        player.sendActionBar("§a" + NewConfig.getString("setup.spawn.action_bar"));

                        if (!settingSpawn.contains(player)) player.sendActionBar(" ");

                    }

                }.runTaskTimer(plugin, 0L, 40L);

            }

            if (name.equals(NewConfig.getString("setup.save.name"))) {

                player.sendMessage(NewConfig.getString("setup.save.done"));
                player.closeInventory();
                Sound.Ping(player);

                config.set("settings.setup", true);

                plugin.saveConfig();

            }

            if (name.equals(NewConfig.getString("kits.add.name"))) {

                new AddKitGUI().openGUI(player);

            }

            if (name.equals(NewConfig.getString("kits.add_gui.set_name.name"))) {

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

            if (name.equals(NewConfig.getString("kits.add_gui.set_icon.name"))) {

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

            if (name.equals(NewConfig.getString("kits.add_gui.set_inventory.name"))) {

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

            if (name.equals(NewConfig.getString("kits.add_gui.next.arrow.name"))) {

                PlayerCache.tempHp.put(player, 20);
                PlayerCache.tempGamemode.put(player, Gamemodes.SURVIVAL);
                new AddKitAttributesGUI().openGUI(player);

            }

            if (name.equals(NewConfig.getString("kits.add_gui.next.health.name").replaceAll("ẞhp", String.valueOf(PlayerCache.tempHp.get(player))))) {

                if (event.isRightClick()) {

                    if (PlayerCache.tempHp.get(player) <= 1 || event.isShiftClick() && PlayerCache.tempHp.get(player) <= 10) {

                        player.sendMessage(NewConfig.getString("kits.add_gui.next.health.max"));
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

                String itemName = NewConfig.getString("kits.add_gui.next.health.name").replaceAll("ẞhp", String.valueOf(PlayerCache.tempHp.get(player)));

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setDisplayName(itemName);

                item.setItemMeta(itemMeta);

            }

            if (name.equals(NewConfig.getString("kits.add_gui.next.gamemode.name"))) {

                Sound.Click(player);

                if (PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL) PlayerCache.tempGamemode.put(player, Gamemodes.ADVENTURE);
                else PlayerCache.tempGamemode.put(player, Gamemodes.SURVIVAL);

                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setLore(List.of(
                        NewConfig.getStringList("kits.add_gui.next.gamemode.lore").get(0).replaceAll("ẞa", PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL ? "§a§u" : "§7"),
                        NewConfig.getStringList("kits.add_gui.next.gamemode.lore").get(1).replaceAll("ẞb", PlayerCache.tempGamemode.get(player) == Gamemodes.ADVENTURE ? "§a§u" : "§7")
                ));

                item.setItemMeta(itemMeta);

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
                    new SetupGUI().openGUI(player);

                    config.set("settings.spawn", loc);
                    plugin.saveConfig();

                });

                return;

            }

            Sound.Error(player);
            player.sendActionBar("§c" + NewConfig.getString("setup.spawn.action_bar"));

        }

        if (addingKitName.contains(player)) {

            event.setCancelled(true);

            boolean exists = false;
            List<?> kitsList = kits.getList("kits");

            if (!kitsList.isEmpty()) {

                for (int i = 0; i < kitsList.size(); i++) {

                    if (kits.getString("kits." + i + ".name").equalsIgnoreCase(message)) exists = true;

                }

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

                    items.addAll(Arrays.asList(player.getInventory().getContents()));

                    PlayerCache.tempKit.put(player, items);

                    new AddKitGUI().openGUI(player);

                });

                return;

            }

            Sound.Error(player);
            player.sendActionBar("§c" + NewConfig.getString("kits.add_gui.set_inventory.action_bar"));

        }

    }

    @EventHandler
    public void onChatMessageSent(AsyncPlayerChatEvent event) {

        event.getRecipients().removeIf(player -> muteChat.contains(player));

    }

}
