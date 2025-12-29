package mel.Polokalap.duelity.Managers;

import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.time.Duration;
import java.util.Iterator;

public class DuelManager {

    private static Main plugin = Main.getInstance();

    public static void join(Player player) {

        PlayerCache.duelEnd.put(player, false);
        PlayerCache.duelPreGameMode.put(player, player.getGameMode());

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 10, true, false, false));

        Sound.Start(player);

        Title title = Title.title(
                Component.text(NewConfig.getString("duel.accepted")),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(300),
                        Duration.ofSeconds(2),
                        Duration.ofMillis(300)
                )
        );

        player.showTitle(title);

        PlayerCache.playerInventory.put(player, player.getInventory().getContents());

    }

    public static void leave(Player player, boolean direct, boolean instant) {

        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));

        PlayerCache.duelTeams.remove(player);
        player.setMaxHealth(20.0d);
        PlayerCache.duelEnd.put(player, true);

        if (instant) {

            player.setInvulnerable(false);
            Player opponent = PlayerCache.duelOpponent.get(player);
            leave(opponent, false, false);
            player.getInventory().clear();
            player.teleport(PlayerCache.duelPreLocation.get(player));
            player.setGameMode(PlayerCache.duelPreGameMode.get(player));
            player.getInventory().setContents(PlayerCache.playerInventory.get(player));
            PlayerCache.inDuel.remove(player);
            return;

        }

        Iterator<Player> iterator = PlayerCache.spectating.iterator();

        while (iterator.hasNext()) {
            Player spectator = iterator.next();
            Player spectatingTarget = PlayerCache.spectatingPlayer.get(spectator);

            if (spectatingTarget != null && (spectatingTarget.equals(player))) {

                spectator.teleport(PlayerCache.spectatePreLocation.get(spectator));
                spectator.setGameMode(PlayerCache.spectatePreGameMode.get(spectator));

                iterator.remove();
                PlayerCache.spectatingPlayer.remove(spectator);

            }

        }

        Sound.Quit(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 10, true, false, false)), 20L);

        PlayerCache.inDuel.remove(player);

        player.getInventory().clear();

        Player opponent = PlayerCache.duelOpponent.get(player);

        Title indirectTitle = Title.title(
                Component.text(NewConfig.getString("duel.left.title").replaceAll("%player%", opponent.getName())),
                Component.text(NewConfig.getString("duel.left.subtitle")),
                Title.Times.times(
                        Duration.ofMillis(300),
                        Duration.ofSeconds(3),
                        Duration.ofMillis(300)
                )
        );

        Title directTitle = Title.title(
                Component.text(NewConfig.getString("duel.left_player.title")),
                Component.text(NewConfig.getString("duel.left_player.subtitle")),
                Title.Times.times(
                        Duration.ofMillis(300),
                        Duration.ofSeconds(3),
                        Duration.ofMillis(300)
                )
        );

        if (direct) player.showTitle(directTitle);
        else player.showTitle(indirectTitle);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            if (direct) player.showTitle(directTitle);
            else player.showTitle(indirectTitle);

        }, 20L);

        if (direct) leave(opponent, false, false);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            player.teleport(PlayerCache.duelPreLocation.get(player));
            player.setGameMode(PlayerCache.duelPreGameMode.get(player));
            player.getInventory().clear();
            player.getInventory().setContents(PlayerCache.playerInventory.get(player));
            player.teleport(PlayerCache.duelPreLocation.get(player));
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), 1L);

        }, 80L);

    }

    public static void end(Player loser, Player winner) {

        Iterator<Player> iterator = PlayerCache.spectating.iterator();

        while (iterator.hasNext()) {
            Player spectator = iterator.next();
            Player spectatingTarget = PlayerCache.spectatingPlayer.get(spectator);

            if (spectatingTarget != null && (spectatingTarget.equals(winner) || spectatingTarget.equals(loser))) {

                spectator.teleport(PlayerCache.spectatePreLocation.get(spectator));
                spectator.setGameMode(PlayerCache.spectatePreGameMode.get(spectator));

                iterator.remove();
                PlayerCache.spectatingPlayer.remove(spectator);

            }

        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> winner.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 10, true, false, false)), 20L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> loser.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 10, true, false, false)), 20L);

        PlayerCache.duelBlueScores.remove(winner);
        PlayerCache.duelBlueScores.remove(loser);
        PlayerCache.duelRedScores.remove(winner);
        PlayerCache.duelRedScores.remove(loser);

        PlayerCache.inDuel.remove(winner);
        PlayerCache.inDuel.remove(loser);

        Title endTitle = Title.title(
                Component.text(NewConfig.getString("duel.end.title")),
                Component.text(NewConfig.getString("duel.end.subtitle").replaceAll("%player%", winner.getName())),
                Title.Times.times(
                        Duration.ofMillis(300),
                        Duration.ofSeconds(3),
                        Duration.ofMillis(300)
                )
        );

        winner.showTitle(endTitle);
        loser.showTitle(endTitle);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            for (Entity entity : winner.getWorld().getNearbyEntities(winner.getLocation(), 300, 300, 300)) {

                if (!(entity instanceof EnderPearl pearl)) continue;

                ProjectileSource shooter = pearl.getShooter();
                if (!(shooter instanceof Player thrower)) continue;

                if (thrower.equals(winner) || thrower.equals(loser)) {

                    pearl.remove();

                }

            }

            winner.teleport(PlayerCache.duelPreLocation.get(winner));
            winner.setGameMode(PlayerCache.duelPreGameMode.get(winner));
            winner.getInventory().clear();

            ItemStack[] winnerSaved = PlayerCache.playerInventory.get(winner);
            if (winnerSaved != null) {
                winner.getInventory().setContents(winnerSaved);
            }

            winner.teleport(PlayerCache.duelPreLocation.get(winner));
            Bukkit.getScheduler().runTaskLater(plugin, () -> winner.setInvulnerable(false), 1L);

            loser.teleport(PlayerCache.duelPreLocation.get(loser));
            loser.setGameMode(PlayerCache.duelPreGameMode.get(loser));
            loser.getInventory().clear();

            ItemStack[] loserSaved = PlayerCache.playerInventory.get(loser);
            if (loserSaved != null) {
                loser.getInventory().setContents(loserSaved);
            }

            loser.teleport(PlayerCache.duelPreLocation.get(loser));
            Bukkit.getScheduler().runTaskLater(plugin, () -> loser.setInvulnerable(false), 1L);

        }, 80L);

    }

}
