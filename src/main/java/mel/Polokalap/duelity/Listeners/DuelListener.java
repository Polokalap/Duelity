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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
        player.setGameMode(GameMode.SPECTATOR);

        Player opponent = PlayerCache.duelOpponent.get(player);

        Player blue;
        Player red;

        if (PlayerCache.duelTeams.get(opponent).equals(Teams.BLUE)) {

            blue = opponent;
            red = player;

        } else {

            blue = player;
            red = opponent;

        }

        PlayerCache.duelBlueScores.putIfAbsent(opponent, 0);
        PlayerCache.duelRedScores.putIfAbsent(opponent, 0);

        int rounds = PlayerCache.duelRounds.getOrDefault(opponent, 1);

        if (PlayerCache.duelTeams.get(opponent) == Teams.BLUE) {
            PlayerCache.duelBlueScores.put(opponent, PlayerCache.duelBlueScores.get(opponent) + 1);
        } else {
            PlayerCache.duelRedScores.put(opponent, PlayerCache.duelRedScores.get(opponent) + 1);
        }

        player.sendMessage(NewConfig.getStringCompiled("duel.round_message")
                .replaceAll("%player%", opponent.getName())
                .replaceAll("%blue_score%", String.valueOf(PlayerCache.duelBlueScores.get(blue)))
                .replaceAll("%red_score%", String.valueOf(PlayerCache.duelRedScores.get(red)))
                .replaceAll("%player_health%", String.valueOf(Math.round(opponent.getHealth() * 100.0) / 100.0))
                        .replaceAll("%max_health%", String.valueOf(opponent.getMaxHealth()))
        );

        opponent.sendMessage(NewConfig.getStringCompiled("duel.round_message")
                .replaceAll("%player%", opponent.getName())
                .replaceAll("%blue_score%", String.valueOf(PlayerCache.duelBlueScores.get(blue)))
                .replaceAll("%red_score%", String.valueOf(PlayerCache.duelRedScores.get(red)))
                .replaceAll("%player_health%", String.valueOf(Math.round(opponent.getHealth() * 100.0) / 100.0))
                .replaceAll("%max_health%", String.valueOf(opponent.getMaxHealth()))
        );

        for (Player spectators : PlayerCache.spectating) {

            if (!PlayerCache.spectatingPlayer.get(spectators).equals(player) && !PlayerCache.spectatingPlayer.get(spectators).equals(opponent)) return;

            spectators.sendMessage(NewConfig.getStringCompiled("duel.round_message")
                    .replaceAll("%player%", opponent.getName())
                    .replaceAll("%blue_score%", String.valueOf(PlayerCache.duelBlueScores.get(blue)))
                    .replaceAll("%red_score%", String.valueOf(PlayerCache.duelRedScores.get(red)))
                    .replaceAll("%player_health%", String.valueOf(Math.round(opponent.getHealth() * 100.0) / 100.0))
                    .replaceAll("%max_health%", String.valueOf(opponent.getMaxHealth()))
            );

        }

        if (PlayerCache.duelRedScores.get(opponent) >= rounds || PlayerCache.duelBlueScores.get(opponent) >= rounds) {

            DuelManager.end(player, opponent);
            return;

        }

        Sound.Won(opponent);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            String kitName = PlayerCache.duelKit.get(player);

            ConfigurationSection kit = KitUtil.getItems(kitName);
            ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");
            World arenaWorld = PlayerCache.worlds.get(0);

            List<ConfigurationSection> activeArenas = new ArrayList<>();

            List<String> allowed = kit.getStringList("arenas");

            player.setInvulnerable(true);
            opponent.setInvulnerable(true);

            for (String key : arenas.getKeys(false)) {

                ConfigurationSection arena = arenas.getConfigurationSection(key);
                if (arena == null) continue;

                String name = arena.getString("name");
                if (name == null) continue;

                if (allowed.contains(name)) {

                    activeArenas.add(arena);

                }

            }

            if (activeArenas.isEmpty()) {

                Bukkit.getLogger().info("There are no arenas for " + kit.getString("name"));
                return;

            }

            Random r = new Random();
            ConfigurationSection starterArena = activeArenas.get(r.nextInt(activeArenas.size()));

            Location placePos = new Location(arenaWorld, PlayerCache.offset, 100, 0);
            WorldEdit.placeSchem(placePos, starterArena.getString("name"));

            PlayerCache.offset += 1500;

            for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 300, 300, 300)) {

                if (!(entity instanceof EnderPearl pearl)) continue;

                ProjectileSource shooter = pearl.getShooter();
                if (!(shooter instanceof Player thrower)) continue;

                if (thrower.equals(player) || thrower.equals(opponent)) {

                    pearl.remove();

                }

            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
                opponent.getActivePotionEffects().forEach(e -> opponent.removePotionEffect(e.getType()));

                KitUtil.claimPlayerKit(kitName, player, player);
                KitUtil.claimPlayerKit(kitName, opponent, opponent);

                player.setGameMode(GameMode.valueOf(kit.getString("gamemode")));
                opponent.setGameMode(GameMode.valueOf(kit.getString("gamemode")));

                player.setMaxHealth(kit.getDouble("health"));
                opponent.setMaxHealth(kit.getDouble("health"));

                placePos.getChunk().load(true);

                Vector blueOffset = new Vector(
                        starterArena.getDouble("blue.x"),
                        starterArena.getDouble("blue.y"),
                        starterArena.getDouble("blue.z")
                );

                Vector redOffset = new Vector(
                        starterArena.getDouble("red.x"),
                        starterArena.getDouble("red.y"),
                        starterArena.getDouble("red.z")
                );

                Location blueSpawn = placePos.clone().add(blueOffset);
                Location redSpawn = placePos.clone().add(redOffset);

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 300, 300, 300)) {

                    if (!(entity instanceof EnderPearl pearl)) continue;

                    ProjectileSource shooter = pearl.getShooter();
                    if (!(shooter instanceof Player thrower)) continue;

                    if (thrower.equals(player) || thrower.equals(opponent)) {

                        pearl.remove();

                    }

                }

                player.teleport(blueSpawn);
                opponent.teleport(redSpawn);

                PlayerCache.canSkip.add(player);
                PlayerCache.canSkip.add(opponent);

                boolean[] instantStart = { false };

                new BukkitRunnable() {

                    int countdown = 5;

                    @Override
                    public void run() {

                        if (countdown < 0) {

                            player.sendActionBar(Component.empty());
                            opponent.sendActionBar(Component.empty());
                            PlayerCache.canSkip.remove(player);
                            PlayerCache.canSkip.remove(opponent);
                            cancel();
                            return;

                        }

                        if (!PlayerCache.canSkip.contains(player) || !PlayerCache.canSkip.contains(opponent)) {

                            player.sendActionBar(Component.empty());
                            opponent.sendActionBar(Component.empty());
                            Sound.Won(player);
                            Sound.Won(opponent);
                            cancel();
                            return;

                        }

                        if (PlayerCache.skipped.contains(player) && PlayerCache.skipped.contains(opponent)) {

                            player.sendActionBar(Component.empty());
                            opponent.sendActionBar(Component.empty());
                            instantStart[0] = true;
                            cancel();
                            return;

                        }

                        player.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", PlayerCache.skipped.contains(player) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));
                        opponent.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", PlayerCache.skipped.contains(opponent) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

                        countdown--;

                    }

                }.runTaskTimer(plugin, 0L, 20L);

                for (Player spectators : PlayerCache.spectating) {

                    if (!PlayerCache.spectatingPlayer.get(spectators).equals(player) && !PlayerCache.spectatingPlayer.get(spectators).equals(opponent)) return;

                    spectators.teleport(PlayerCache.spectatingPlayer.get(spectators));

                }

                player.setHealth(player.getMaxHealth());
                opponent.setHealth(opponent.getMaxHealth());

                player.setSaturation(3);
                opponent.setSaturation(3);

                player.setFoodLevel(20);
                opponent.setFoodLevel(20);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                    player.lookAt(opponent.getEyeLocation(), LookAnchor.EYES);
                    opponent.lookAt(player.getEyeLocation(), LookAnchor.EYES);

                }, 1L);

                PlayerCache.duelSpectators.put(player, plugin.getPlayerConfig().getBoolean("players." + opponent.getUniqueId() + ".settings.spectate"));
                PlayerCache.duelSpectators.put(opponent, plugin.getPlayerConfig().getBoolean("players." + opponent.getUniqueId() + ".settings.spectate"));
                PlayerCache.duelRequests.remove(opponent);

                new BukkitRunnable() {

                    int countdown = 6;

                    @Override
                    public void run() {

                        if (PlayerCache.duelEnd.get(player)) cancel();

                        if (countdown <= 0 || instantStart[0]) {

                            Title timerTitle = Title.title(
                                    Component.text(NewConfig.getString("duel.duel_start.title")),
                                    Component.text(NewConfig.getString("duel.duel_start.subtitle")),
                                    Title.Times.times(
                                            Duration.ofMillis(300),
                                            Duration.ofSeconds(2),
                                            Duration.ofMillis(300)
                                    )
                            );

                            player.showTitle(timerTitle);
                            opponent.showTitle(timerTitle);

                            Sound.Ping(player);
                            Sound.Ping(opponent);

                            player.setInvulnerable(false);
                            opponent.setInvulnerable(false);

                            cancel();

                        } else {

                            if (countdown <= 5) {

                                Title timerTitle = Title.title(
                                        Component.text(NewConfig.getString("duel.countdown.title").replaceAll("%time%", String.valueOf(countdown))),
                                        Component.text(NewConfig.getString("duel.countdown.subtitle")
                                                .replaceAll("%blue_score%", String.valueOf(PlayerCache.duelBlueScores.get(blue)))
                                                .replaceAll("%red_score%", String.valueOf(PlayerCache.duelRedScores.get(red)))
                                                .replaceAll("%rounds%", String.valueOf(PlayerCache.duelRounds.get(player)))
                                        ),
                                        Title.Times.times(
                                                Duration.ofMillis(300),
                                                Duration.ofSeconds(1),
                                                Duration.ofMillis(300)
                                        )
                                );

                                if (PlayerCache.duelEnd.get(player)) cancel();

                                player.showTitle(timerTitle);
                                opponent.showTitle(timerTitle);

                                Sound.Click(player);
                                Sound.Click(opponent);

                            }

                            countdown--;

                        }

                        if (PlayerCache.duelEnd.get(player)) cancel();

                    }

                }.runTaskTimer(plugin, 0L, 20L);

            }, 60L);

        }, 20L);

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

}
