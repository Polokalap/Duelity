package mel.Polokalap.duelity.Utils;

import io.papermc.paper.entity.LookAnchor;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.DuelManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Duel {

    private static final Main plugin = Main.getInstance();

    private static String name;
    private static Player player1;
    private static Player player2;
    private static ConfigurationSection kit;
    private static ConfigurationSection arenas;
    private static int rounds;
    private static boolean allowSpectators;

    private static boolean isWaiting;
    private static ArrayList<Player> spectators;
    private static int blueScore;
    private static int redScore;
    private static HashMap<Player, Teams> teams;
    private static HashMap<Teams, Integer> roundsCount;
    private static HashMap<Player, Boolean> skip;

    public Duel(Player player1, Player player2, String name, ConfigurationSection kit, ConfigurationSection arenas, int rounds, Boolean allowSpectators) {

        this.name = name;
        this.player1 = player1;
        this.player2 = player2;
        this.kit = kit;
        this.arenas = arenas;
        this.rounds = rounds;
        this.allowSpectators = allowSpectators;

        this.spectators = new ArrayList<>();
        this.blueScore = 0;
        this.redScore = 0;
        this.teams = new HashMap<>();
        this.skip = new HashMap<>();

        this.teams.put(player1, Teams.BLUE);
        this.teams.put(player2, Teams.RED);

        roundsCount = new HashMap<>();

        roundsCount.put(Teams.BLUE, 0);
        roundsCount.put(Teams.RED, 0);

        blueScore = 0;
        redScore = 0;

        isWaiting = false;

    }

    public List<Player> getPlayers() {

        return List.of(player1, player2);

    }

    public String getName() {

        return name;

    }

    public ConfigurationSection getKit() {

        return kit;

    }

    public int getRounds() {

        return rounds;

    }

    public boolean getAllowSpectators() {

        return allowSpectators;

    }

    public ArrayList<Player> getSpectators() {

        return spectators;

    }

    public boolean canSkip() {

        return isWaiting;

    }

    public Player getOpponent(Player player) {

        if (player.equals(player1)) return player2;
        else return player1;

    }

    public Player getBlue() {

        return player1;

    }

    public Player getRed() {

        return player2;

    }

    public void skip(Player player) {

        skip.put(player, true);

    }

    public boolean hasSkipped(Player player) {

        return skip.get(player);

    }

    public void startDuel() {

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
            return;

        }

        player1.setInvulnerable(true);
        player2.setInvulnerable(true);

        PlayerCache.skipped.remove(player1);
        PlayerCache.skipped.remove(player2);

        player1.getActivePotionEffects().forEach(e -> player1.removePotionEffect(e.getType()));
        player2.getActivePotionEffects().forEach(e -> player2.removePotionEffect(e.getType()));

        DuelManager.join(player1);
        DuelManager.join(player2);

        PlayerCache.duelPreLocation.put(player1, player1.getLocation());
        PlayerCache.duelPreLocation.put(player2, player2.getLocation());

        PlayerCache.duelRequests.remove(player1);
        PlayerCache.duelRequests.remove(player2);

        Random r = new Random();
        ConfigurationSection starterArena = activeArenas.get(r.nextInt(activeArenas.size()));

        Location placePos = new Location(arenaWorld, PlayerCache.offset, 100, 0);
        WorldEdit.placeSchem(placePos, starterArena.getString("name"));

        PlayerCache.offset += 1500;

        PlayerCache.preInDuel.add(player1);
        PlayerCache.preInDuel.add(player2);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            player1.getActivePotionEffects().forEach(e -> player1.removePotionEffect(e.getType()));
            player2.getActivePotionEffects().forEach(e -> player2.removePotionEffect(e.getType()));

            PlayerCache.preInDuel.remove(player1);
            PlayerCache.preInDuel.remove(player2);
            PlayerCache.inDuel.add(player1);
            PlayerCache.inDuel.add(player2);

            KitUtil.claimPlayerKit(name, player1, player1);
            KitUtil.claimPlayerKit(name, player2, player2);

            player1.setGameMode(GameMode.valueOf(kit.getString("gamemode")));
            player2.setGameMode(GameMode.valueOf(kit.getString("gamemode")));

            player1.setMaxHealth(kit.getDouble("health"));
            player2.setMaxHealth(kit.getDouble("health"));

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

            player1.teleport(blueSpawn);
            player2.teleport(redSpawn);

            PlayerCache.canSkip.add(player2);
            PlayerCache.canSkip.add(player2);

            boolean[] instantStart = { false };

            new BukkitRunnable() {

                int countdown = 5;

                @Override
                public void run() {

                    if (countdown < 0) {

                        player1.sendActionBar(Component.empty());
                        player2.sendActionBar(Component.empty());
                        PlayerCache.canSkip.remove(player1);
                        PlayerCache.canSkip.remove(player2);
                        isWaiting = false;
                        cancel();
                        return;

                    }

                    if (!PlayerCache.canSkip.contains(player1) || !PlayerCache.canSkip.contains(player2)) {

                        player1.sendActionBar(Component.empty());
                        player2.sendActionBar(Component.empty());
                        Sound.Won(player1);
                        Sound.Won(player2);
                        isWaiting = false;
                        cancel();
                        return;

                    }

                    if (PlayerCache.skipped.contains(player1) && PlayerCache.skipped.contains(player2)) {

                        player1.sendActionBar(Component.empty());
                        player2.sendActionBar(Component.empty());
                        instantStart[0] = true;
                        isWaiting = false;
                        cancel();
                        return;

                    }

                    player1.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", PlayerCache.skipped.contains(player1) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));
                    player2.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", PlayerCache.skipped.contains(player2) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

                    countdown--;

                }

            }.runTaskTimer(plugin, 0L, 20L);

            PlayerCache.duelTeams.put(player1, Teams.BLUE);
            PlayerCache.duelTeams.put(player2, Teams.RED);

            player1.setHealth(player1.getMaxHealth());
            player2.setHealth(player2.getMaxHealth());

            player1.setSaturation(3);
            player2.setSaturation(3);

            player1.setFoodLevel(20);
            player2.setFoodLevel(20);

            player1.lookAt(player2.getEyeLocation(), LookAnchor.EYES);
            player2.lookAt(player1.getEyeLocation(), LookAnchor.EYES);

            new BukkitRunnable() {

                int countdown = 6;

                @Override
                public void run() {

                    if (PlayerCache.duelEnd.get(player1)) cancel();

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

                        player1.showTitle(timerTitle);
                        player2.showTitle(timerTitle);

                        Sound.Ping(player1);
                        Sound.Ping(player2);

                        player1.setInvulnerable(false);
                        player2.setInvulnerable(false);

                        cancel();

                    } else {

                        if (countdown <= 5) {

                            Title timerTitle = Title.title(
                                    Component.text(NewConfig.getString("duel.countdown.title").replaceAll("%time%", String.valueOf(countdown))),
                                    Component.text(NewConfig.getString("duel.countdown.subtitle")
                                            .replaceAll("%blue_score%", String.valueOf(blueScore))
                                            .replaceAll("%red_score%", String.valueOf(redScore))
                                            .replaceAll("%rounds%", String.valueOf(rounds))
                                    ),
                                    Title.Times.times(
                                            Duration.ofMillis(300),
                                            Duration.ofSeconds(1),
                                            Duration.ofMillis(300)
                                    )
                            );

                            player1.showTitle(timerTitle);
                            player2.showTitle(timerTitle);

                            Sound.Click(player1);
                            Sound.Click(player2);

                        }

                        countdown--;

                    }

                }

            }.runTaskTimer(plugin, 0L, 20L);

        }, 60);

    }

    public void endRound(Player loser) {

        skip.put(player1, false);
        skip.put(player2, false);

        Player winner = getOpponent(loser);

        loser.setGameMode(GameMode.SPECTATOR);

        Player blue = player1;
        Player red = player2;

        if (winner.equals(player1)) {

            blueScore += 1;
            roundsCount.put(Teams.BLUE, roundsCount.get(Teams.BLUE) + 1);

        }
        else {

            redScore += 1;
            roundsCount.put(Teams.RED, roundsCount.get(Teams.RED) + 1);

        }

        loser.sendMessage(NewConfig.getStringCompiled("duel.round_message")
                .replaceAll("%player%", winner.getName())
                .replaceAll("%blue_score%", String.valueOf(blueScore))
                .replaceAll("%red_score%", String.valueOf(redScore))
                .replaceAll("%player_health%", String.valueOf(Math.round(winner.getHealth() * 100.0) / 100.0))
                .replaceAll("%max_health%", String.valueOf(winner.getMaxHealth()))
        );

        winner.sendMessage(NewConfig.getStringCompiled("duel.round_message")
                .replaceAll("%player%", winner.getName())
                .replaceAll("%blue_score%", String.valueOf(blueScore))
                .replaceAll("%red_score%", String.valueOf(redScore))
                .replaceAll("%player_health%", String.valueOf(Math.round(winner.getHealth() * 100.0) / 100.0))
                .replaceAll("%max_health%", String.valueOf(winner.getMaxHealth()))
        );

        for (Player spectators : PlayerCache.spectating) {

            if (!PlayerCache.spectatingPlayer.get(spectators).equals(loser) && !PlayerCache.spectatingPlayer.get(spectators).equals(winner)) continue;

            spectators.sendMessage(NewConfig.getStringCompiled("duel.round_message")
                    .replaceAll("%player%", winner.getName())
                    .replaceAll("%blue_score%", String.valueOf(blueScore))
                    .replaceAll("%red_score%", String.valueOf(redScore))
                    .replaceAll("%player_health%", String.valueOf(Math.round(winner.getHealth() * 100.0) / 100.0))
                    .replaceAll("%max_health%", String.valueOf(winner.getMaxHealth()))
            );

        }

        if (blueScore >= rounds || redScore >= rounds) {

            DuelManager.end(loser, winner);
            return;

        }

        Sound.Won(winner);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            ConfigurationSection kit = KitUtil.getItems(name);
            ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");
            World arenaWorld = PlayerCache.worlds.get(0);

            List<ConfigurationSection> activeArenas = new ArrayList<>();

            List<String> allowed = kit.getStringList("arenas");

            loser.setInvulnerable(true);
            winner.setInvulnerable(true);

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

            for (Entity entity : loser.getWorld().getNearbyEntities(loser.getLocation(), 300, 300, 300)) {

                if (!(entity instanceof EnderPearl pearl)) continue;

                ProjectileSource shooter = pearl.getShooter();
                if (!(shooter instanceof Player thrower)) continue;

                if (thrower.equals(loser) || thrower.equals(winner)) {

                    pearl.remove();

                }

            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                loser.getActivePotionEffects().forEach(e -> loser.removePotionEffect(e.getType()));
                winner.getActivePotionEffects().forEach(e -> winner.removePotionEffect(e.getType()));

                loser.closeInventory();
                loser.getInventory().clear();
                winner.closeInventory();
                winner.getInventory().clear();

                KitUtil.claimPlayerKit(name, loser, loser);
                KitUtil.claimPlayerKit(name, winner, winner);

                loser.setGameMode(GameMode.valueOf(kit.getString("gamemode")));
                winner.setGameMode(GameMode.valueOf(kit.getString("gamemode")));

                loser.setMaxHealth(kit.getDouble("health"));
                winner.setMaxHealth(kit.getDouble("health"));

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

                for (Entity entity : loser.getWorld().getNearbyEntities(loser.getLocation(), 300, 300, 300)) {

                    if (!(entity instanceof EnderPearl pearl)) continue;

                    ProjectileSource shooter = pearl.getShooter();
                    if (!(shooter instanceof Player thrower)) continue;

                    if (thrower.equals(loser) || thrower.equals(winner)) {

                        pearl.remove();

                    }

                }

                loser.teleport(blueSpawn);
                winner.teleport(redSpawn);

                PlayerCache.skipped.remove(loser);
                PlayerCache.skipped.remove(winner);
                PlayerCache.canSkip.add(loser);
                PlayerCache.canSkip.add(winner);

                isWaiting = true;

                boolean[] instantStart = { false };

                new BukkitRunnable() {

                    int countdown = 5;

                    @Override
                    public void run() {

                        if (countdown < 0) {

                            loser.sendActionBar(Component.empty());
                            winner.sendActionBar(Component.empty());
                            PlayerCache.canSkip.remove(loser);
                            PlayerCache.canSkip.remove(winner);
                            isWaiting = false;
                            cancel();
                            return;

                        }

                        if (!PlayerCache.canSkip.contains(loser) || !PlayerCache.canSkip.contains(winner)) {

                            loser.sendActionBar(Component.empty());
                            winner.sendActionBar(Component.empty());
                            PlayerCache.canSkip.remove(loser);
                            PlayerCache.canSkip.remove(winner);
                            Sound.Won(loser);
                            Sound.Won(winner);
                            isWaiting = false;
                            cancel();
                            return;

                        }

                        if (skip.get(player1) && skip.get(player2)) {

                            loser.sendActionBar(Component.empty());
                            winner.sendActionBar(Component.empty());
                            PlayerCache.canSkip.remove(loser);
                            PlayerCache.canSkip.remove(winner);
                            instantStart[0] = true;
                            isWaiting = false;
                            cancel();
                            return;

                        }

                        player1.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", skip.get(player1) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));
                        player2.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", skip.get(player2) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

                        countdown--;

                    }

                }.runTaskTimer(plugin, 0L, 20L);

                for (Player spectators : PlayerCache.spectating) {

                    if (!PlayerCache.spectatingPlayer.get(spectators).equals(loser) && !PlayerCache.spectatingPlayer.get(spectators).equals(winner)) continue;

                    spectators.teleport(PlayerCache.spectatingPlayer.get(spectators));

                }

                loser.setHealth(loser.getMaxHealth());
                winner.setHealth(winner.getMaxHealth());

                loser.setSaturation(3);
                winner.setSaturation(3);

                loser.setFoodLevel(20);
                winner.setFoodLevel(20);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                    loser.lookAt(winner.getEyeLocation(), LookAnchor.EYES);
                    winner.lookAt(loser.getEyeLocation(), LookAnchor.EYES);

                }, 1L);

                PlayerCache.duelSpectators.put(loser, allowSpectators);
                PlayerCache.duelSpectators.put(winner, plugin.getPlayerConfig().getBoolean("players." + winner.getUniqueId() + ".settings.spectate"));
                PlayerCache.duelRequests.remove(winner);

                new BukkitRunnable() {

                    int countdown = 6;

                    @Override
                    public void run() {

                        if (PlayerCache.duelEnd.get(loser)) cancel();

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

                            loser.showTitle(timerTitle);
                            winner.showTitle(timerTitle);

                            Sound.Ping(loser);
                            Sound.Ping(winner);

                            loser.setInvulnerable(false);
                            winner.setInvulnerable(false);

                            cancel();

                        } else {

                            if (countdown <= 5) {

                                Title timerTitle = Title.title(
                                        Component.text(NewConfig.getString("duel.countdown.title").replaceAll("%time%", String.valueOf(countdown))),
                                        Component.text(NewConfig.getString("duel.countdown.subtitle")
                                                .replaceAll("%blue_score%", String.valueOf(blueScore))
                                                .replaceAll("%red_score%", String.valueOf(redScore))
                                                .replaceAll("%rounds%", String.valueOf(rounds))
                                        ),
                                        Title.Times.times(
                                                Duration.ofMillis(300),
                                                Duration.ofSeconds(1),
                                                Duration.ofMillis(300)
                                        )
                                );

                                if (PlayerCache.duelEnd.get(loser)) cancel();

                                loser.showTitle(timerTitle);
                                winner.showTitle(timerTitle);

                                Sound.Click(loser);
                                Sound.Click(winner);

                            }

                            countdown--;

                        }

                        if (PlayerCache.duelEnd.get(loser)) cancel();

                    }

                }.runTaskTimer(plugin, 0L, 20L);

            }, 60L);

        }, 20L);

    }

    public static void addSpectator(Player spectator) {

        spectators.add(spectator);

    }

}
