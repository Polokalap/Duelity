package mel.Polokalap.duelity.Listeners;

import io.papermc.paper.entity.LookAnchor;
import mel.Polokalap.duelity.GUI.DuelGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.DuelManager;
import mel.Polokalap.duelity.Utils.*;
import mel.Polokalap.duelity.Utils.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.*;

public class DuelListener implements Listener {

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

                    player.closeInventory();

                    Player opponent = PlayerCache.duelOpponent.get(player).getPlayer();

                    if (opponent != null && opponent.isOnline()) {

                        if (PlayerCache.spectating.contains(player)) {

                            Sound.Error(player);
                            player.sendMessage(NewConfig.getString("spectate.in_spectator").replaceAll("%player%", opponent.getName()));
                            return;

                        }

                        if (PlayerCache.inDuel.contains(player) || PlayerCache.preInDuel.contains(player)) {

                            Sound.Error(player);
                            player.sendMessage(NewConfig.getString("duel.in_duel").replaceAll("%player%", opponent.getName()));
                            return;

                        }

                        HashMap<Player, String> request = new HashMap<>();

                        request.put(opponent, (String) kit.get("name"));

                        PlayerCache.duelRequests.put(player, request);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {

                            Player cachedOpponent = PlayerCache.duelOpponent.get(player);
                            if (cachedOpponent == null || !cachedOpponent.equals(opponent)) return;

                            Map<Player, ?> requests = PlayerCache.duelRequests.get(player);
                            if (requests == null || !requests.containsKey(cachedOpponent)) return;

                            if (player.isOnline()) {
                                player.sendMessage(NewConfig.getString("duel.sender.expired").replace("%player%", opponent.getName()));
                            }

                            if (opponent.isOnline()) {
                                opponent.sendMessage(NewConfig.getString("duel.receiver.expired").replace("%player%", player.getName()));
                            }

                            PlayerCache.duelOpponent.remove(player);
                            PlayerCache.duelRequests.remove(player);

                        }, config.getLong("duel.duel_timeout") * 20L);

                        Sound.Ping(player);

                        Component accept_button = Component.text(NewConfig.getString("duel.message.accept.text"))
                                .hoverEvent(HoverEvent.showText(
                                        Component.text(NewConfig.getString("duel.message.accept.hover")))
                                ).clickEvent(ClickEvent.runCommand("/acceptduel " + player.getName()));

                        Component decline_button = Component.text(NewConfig.getString("duel.message.decline.text"))
                                .hoverEvent(HoverEvent.showText(
                                        Component.text(NewConfig.getString("duel.message.decline.hover"))
                                                )
                                ).clickEvent(ClickEvent.runCommand("/declineduel " + player.getName()));

                        Component information_button = Component.text(NewConfig.getString("duel.message.information.text"))
                                .hoverEvent(HoverEvent.showText(
                                        Component.text(NewConfig.getStringCompiled("duel.message.information.hover")
                                                .replaceAll("ẞopponent", player.getName())
                                                .replaceAll("ẞkit", (String) kit.get("name"))
                                                .replaceAll("ẞrounds", String.valueOf(playerSettings.getInt("rounds")))
                                                .replaceAll("ẞspectators", playerSettings.getBoolean("spectate") ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))
                                        )
                                ));

                        opponent.sendMessage(NewConfig.getComponentList(
                                "duel.message.receiver",
                                Placeholder.unparsed("player", player.getName()),
                                Placeholder.component("accept", accept_button),
                                Placeholder.component("decline", decline_button),
                                Placeholder.component("information", information_button)
                        ));

                        Component cancel_button = Component.text(NewConfig.getString("duel.message.cancel.text"))
                                .hoverEvent(HoverEvent.showText(
                                        Component.text(NewConfig.getString("duel.message.cancel.hover"))

                                ))
                                .clickEvent(ClickEvent.runCommand("/cancelduel " + opponent.getName()));

                        player.sendMessage(NewConfig.getComponentList(
                                "duel.message.sender",
                                Placeholder.component("cancel", cancel_button)
                        ));

                    } else {

                        Sound.Error(player);
                        player.sendMessage(NewConfig.getString("duel.offline").replaceAll("%player%", opponent.getName()));
                        return;

                    }

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

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.inDuel.contains(player) && !PlayerCache.preInDuel.contains(player)) return;

        if (!event.getBlock().hasMetadata("player_placed")) event.setCancelled(true);

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.inDuel.contains(player)) return;

        if (PlayerCache.preInDuel.contains(player)) {

            event.setCancelled(true);
            return;

        }

        event.getBlock().setMetadata("player_placed", new FixedMetadataValue(plugin, "player_placed"));

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.inDuel.contains(player) || PlayerCache.preInDuel.contains(player)) DuelManager.leave(player, true, true);

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.inDuel.contains(player)) return;

        event.setCancelled(true);

        Duel duel = PlayerCache.playerDuel.get(player);

        duel.endRound(player);

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.inDuel.contains(player) && !PlayerCache.preInDuel.contains(player)) return;

        String message = event.getMessage();

        if (player.hasPermission("duelity.admin")) return;

        if (!message.equalsIgnoreCase("/leave") && !message.equalsIgnoreCase("/l")) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.inDuel.contains(player) && !PlayerCache.preInDuel.contains(player)) return;

        event.setCancelled(true);

    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;

        if (!PlayerCache.inDuel.contains(player) && !PlayerCache.preInDuel.contains(player)) return;

        ConfigurationSection kit = KitUtil.getItems(PlayerCache.playerDuel.get(player).getName());
        boolean regen = kit.getBoolean("regen");

        if (regen) return;

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) event.setCancelled(true);

    }

}
