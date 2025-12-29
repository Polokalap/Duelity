package mel.Polokalap.duelity.Managers;

import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

public class DuelManager {

    private static Main plugin = Main.getInstance();

    public static void join(Player player) {

        PlayerCache.inDuel.add(player);
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
            player.getInventory().addItem(PlayerCache.playerInventory.get(player));
            PlayerCache.inDuel.remove(player);
            return;

        }

        for (Player spectators : PlayerCache.spectating) {

            if (PlayerCache.spectatingPlayer.get(spectators) != null && PlayerCache.spectatingPlayer.get(spectators).equals(player)) {

                player.teleport(PlayerCache.spectatePreLocation.get(player));
                player.setGameMode(PlayerCache.spectatePreGameMode.get(player));

                PlayerCache.spectating.remove(player);
                PlayerCache.spectatingPlayer.remove(player);

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

        if (direct) leave(opponent, false, false);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            player.teleport(PlayerCache.duelPreLocation.get(player));
            player.setGameMode(PlayerCache.duelPreGameMode.get(player));
            player.getInventory().clear();
            player.getInventory().addItem(PlayerCache.playerInventory.get(player));
            player.teleport(PlayerCache.duelPreLocation.get(player));
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), 1L);

        }, 80L);

    }

    public static void end(Player loser, Player winner) {

        for (Player spectators : PlayerCache.spectating) {

            if (PlayerCache.spectatingPlayer.get(spectators) != null && PlayerCache.spectatingPlayer.get(spectators).equals(winner)) {

                winner.teleport(PlayerCache.spectatePreLocation.get(winner));
                winner.setGameMode(PlayerCache.spectatePreGameMode.get(winner));

                PlayerCache.spectating.remove(winner);
                PlayerCache.spectatingPlayer.remove(winner);

            }

            if (PlayerCache.spectatingPlayer.get(spectators) != null && PlayerCache.spectatingPlayer.get(spectators).equals(loser)) {

                loser.teleport(PlayerCache.spectatePreLocation.get(loser));
                loser.setGameMode(PlayerCache.spectatePreGameMode.get(loser));

                PlayerCache.spectating.remove(loser);
                PlayerCache.spectatingPlayer.remove(loser);

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
