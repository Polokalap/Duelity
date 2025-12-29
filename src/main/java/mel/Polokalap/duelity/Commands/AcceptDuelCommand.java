package mel.Polokalap.duelity.Commands;

import io.papermc.paper.entity.LookAnchor;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.DuelManager;
import mel.Polokalap.duelity.Utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AcceptDuelCommand implements CommandExecutor, TabCompleter {

    Main plugin = Main.getInstance();
    FileConfiguration config = plugin.getConfig();
    FileConfiguration kits = plugin.getKitConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(NewConfig.getString("console.player"));
            return true;

        }

        if (PlayerCache.inDuel.contains(player)) return true;

        if (args.length < 1) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("player.args"));
            return true;

        }

        Player opponent = Bukkit.getPlayerExact(args[0]);

        if (opponent.getUniqueId().equals(player.getUniqueId())) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("duel.self"));
            return true;

        }

        if (PlayerCache.duelRequests.get(opponent) != null && PlayerCache.duelRequests.get(opponent).containsKey(player) || PlayerCache.duelOpponent.get(opponent) != null && PlayerCache.duelOpponent.get(opponent).equals(player)) {

            String kitName = PlayerCache.duelRequests.get(opponent).get(player);

            ConfigurationSection kit = KitUtil.getItems(kitName);
            ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");
            World arenaWorld = PlayerCache.worlds.get(0);

            List<ConfigurationSection> activeArenas = new ArrayList<>();

            List<String> allowed = kit.getStringList("arenas");

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
                return true;

            }

            PlayerCache.duelKit.put(player, kitName);
            PlayerCache.duelKit.put(opponent, kitName);
            PlayerCache.duelRequests.remove(opponent);
            PlayerCache.duelOpponent.put(player, opponent);
            PlayerCache.duelOpponent.put(opponent, player);

            DuelManager.join(player);
            DuelManager.join(opponent);

            player.setInvulnerable(true);
            opponent.setInvulnerable(true);

            PlayerCache.duelPreLocation.put(player, player.getLocation());
            PlayerCache.duelPreLocation.put(opponent, opponent.getLocation());

            PlayerCache.duelBlueScores.put(player, 0);
            PlayerCache.duelBlueScores.put(opponent, 0);
            PlayerCache.duelRedScores.put(player, 0);
            PlayerCache.duelRedScores.put(opponent, 0);

            Random r = new Random();
            ConfigurationSection starterArena = activeArenas.get(r.nextInt(activeArenas.size()));

            Location placePos = new Location(arenaWorld, PlayerCache.offset, 100, 0);
            WorldEdit.placeSchem(placePos, starterArena.getString("name"));

            PlayerCache.offset += 1500;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

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

                player.teleport(blueSpawn);
                opponent.teleport(redSpawn);

                PlayerCache.duelTeams.put(player, Teams.BLUE);
                PlayerCache.duelTeams.put(opponent, Teams.RED);

                player.setHealth(player.getMaxHealth());
                opponent.setHealth(opponent.getMaxHealth());

                player.setSaturation(3);
                opponent.setSaturation(3);

                player.setFoodLevel(20);
                opponent.setFoodLevel(20);

                player.lookAt(opponent.getEyeLocation(), LookAnchor.EYES);
                opponent.lookAt(player.getEyeLocation(), LookAnchor.EYES);

                PlayerCache.duelRounds.put(player, plugin.getPlayerConfig().getInt("players." + opponent.getUniqueId() + ".settings.rounds"));
                PlayerCache.duelRounds.put(opponent, plugin.getPlayerConfig().getInt("players." + opponent.getUniqueId() + ".settings.rounds"));
                PlayerCache.duelSpectators.put(player, plugin.getPlayerConfig().getBoolean("players." + opponent.getUniqueId() + ".settings.spectate"));
                PlayerCache.duelSpectators.put(opponent, plugin.getPlayerConfig().getBoolean("players." + opponent.getUniqueId() + ".settings.spectate"));

                new BukkitRunnable() {

                    int countdown = 6;

                    @Override
                    public void run() {

                        if (PlayerCache.duelEnd.get(player)) cancel();

                        if (countdown <= 0) {

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
                                                .replaceAll("%blue_score%", String.valueOf(PlayerCache.duelBlueScores.get(player)))
                                                .replaceAll("%red_score%", String.valueOf(PlayerCache.duelRedScores.get(opponent)))
                                                .replaceAll("%rounds%", String.valueOf(PlayerCache.duelRounds.get(player)))
                                        ),
                                        Title.Times.times(
                                                Duration.ofMillis(300),
                                                Duration.ofSeconds(1),
                                                Duration.ofMillis(300)
                                        )
                                );

                                player.showTitle(timerTitle);
                                opponent.showTitle(timerTitle);

                                Sound.Click(player);
                                Sound.Click(opponent);

                            }

                            countdown--;

                        }

                    }

                }.runTaskTimer(plugin, 0L, 20L);

            }, 60);

            return true;

        }

        Sound.Error(player);
        player.sendMessage(NewConfig.getString("duel.no_sent_invite"));

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        List<String> players = new ArrayList<>(List.of());

        for (Player player : Bukkit.getOnlinePlayers()) players.add(player.getName());

        switch (args.length) {

            case 1:
                return players;

            default:
                return List.of();

        }

    }

}
