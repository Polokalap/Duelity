package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.PlayerManager;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.ServerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

        if (playerSettings.getBoolean("spectators")) {

            playerSettings.set("spectators", true);

        }

        plugin.savePlayerConfig();

        if (!ServerUtil.isServerSetupDone()) {

            if (player.hasPermission("duelity.admin")) {

                 player.sendMessage(NewConfig.getStringCompiled("admin.setup_incomplete"));

            }

        } else {

            PlayerManager.load(player);

        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.duelRequests.containsKey(player)) PlayerCache.duelRequests.remove(player);

    }

}
