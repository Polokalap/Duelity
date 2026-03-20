package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.PlayerManager;
import mel.Polokalap.duelity.Utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerStateListener implements Listener {

    private Main plugin = Main.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        ConfigurationSection playerSettings = plugin.getPlayerConfig().getConfigurationSection("players." + player.getUniqueId() + ".settings");

        if (playerSettings == null) {

            playerSettings = plugin.getPlayerConfig().createSection("players." + player.getUniqueId() + ".settings");

        }

        if (playerSettings.getInt("rounds", -1) == -1) {

            playerSettings.set("rounds", 3);

        }

        if (!playerSettings.contains("spectators")) {

            playerSettings.set("spectators", true);

        }

        PlayerManager.load(player);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.duelRequests.containsKey(player)) PlayerCache.duelRequests.remove(player);

    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {

        Player player = event.getPlayer();

        Duel duel = PlayerCache.playerDuel.get(player);

        if (duel == null) return;

        if (duel.canSkip()) {

            if (duel.hasSkipped(player)) return;

            Player opponent = duel.getOpponent(player);

            duel.skip(player);

            Sound.Ping(player);
            Sound.Ping(opponent);

            player.sendMessage((PlayerCache.duelTeams.get(player) == Teams.BLUE ? "§9" : "§c") + NewConfig.getString("duel.skipped").replaceAll("ẞplayer", player.getName()));
            opponent.sendMessage((PlayerCache.duelTeams.get(player) == Teams.BLUE ? "§9" : "§c") + NewConfig.getString("duel.skipped").replaceAll("ẞplayer", player.getName()));

            player.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", duel.hasSkipped(player) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));
            opponent.sendActionBar(Component.text(NewConfig.getString("duel.skip").replaceAll("ẞstatus", duel.hasSkipped(opponent) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.inDuel.contains(player)) return;

        if (!event.hasExplicitlyChangedPosition()) return;
        if (event.getTo().getBlockY() < -64) event.setCancelled(true);

    }

}
