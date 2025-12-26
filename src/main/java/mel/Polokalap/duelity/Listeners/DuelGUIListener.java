package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.GUI.DuelGUI;
import mel.Polokalap.duelity.GUI.EditorGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class DuelGUIListener implements Listener {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    private HashMap<Player, Long> now = new HashMap<>();
    private int delay = 250;

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();

        if (inv.getHolder() instanceof DuelGUI) {

            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) return;

            String name = item.getItemMeta().getDisplayName();

            if (now.get(player) == null) now.put(player, System.currentTimeMillis());

            if (System.currentTimeMillis() - now.get(player) < delay) return;

            now.put(player, System.currentTimeMillis());

            ConfigurationSection playerSettings = plugin.getPlayerConfig().getConfigurationSection("players." + player.getUniqueId() + ".settings");

            if (ItemUtil.PDCHelper("duel_rounds", item)) {

                Sound.Click(player);

                int rounds = playerSettings.getInt("rounds");

                if (event.isLeftClick()) rounds++;
                if (event.isRightClick() && rounds > 1) rounds--;

                playerSettings.set("rounds", rounds);

                plugin.savePlayerConfig();

                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(NewConfig.getString("duel.gui.rounds.name").replaceAll("%rounds%", String.valueOf(playerSettings.getInt("rounds"))));

                item.setItemMeta(meta);

            }

            if (ItemUtil.PDCHelper("duel_spectate_toggle", item)) {

                Sound.Click(player);

                boolean toggle = playerSettings.getBoolean("spectate");

                playerSettings.set("spectate", !toggle);

                plugin.savePlayerConfig();

                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(NewConfig.getString("duel.gui.spectate.name").replaceAll("%state%", playerSettings.getBoolean("spectate") ? NewConfig.getString("player.on") : NewConfig.getString("player.off")));

                item.setItemMeta(meta);

            }

            ConfigurationSection kits = plugin.getKitConfig().getConfigurationSection("kits");

            for (String kitId : kits.getKeys(false)) {

                ConfigurationSection kit = kits.getConfigurationSection(kitId);

                if (ItemUtil.PDCHelper("duel-" + kit.get("name"), item)) {

                    Sound.Ping(player);

                    player.closeInventory();

                    PlayerCache.duelRounds.put(player, playerSettings.getInt("rounds"));
                    PlayerCache.duelAllowSpectators.put(player, playerSettings.getBoolean("spectate"));
                    PlayerCache.duelKit.put(player, (String) kit.get("name"));

                }

            }

        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (inv.getHolder() instanceof DuelGUI) {

            Sound.Close(player);

        }

    }

}
