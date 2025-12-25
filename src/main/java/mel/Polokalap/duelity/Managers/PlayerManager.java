package mel.Polokalap.duelity.Managers;

import mel.Polokalap.duelity.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerManager {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static void load(Player player) {

        player.setMaxHealth(20.0d);
        player.setInvulnerable(false);

        for (Player players : Bukkit.getOnlinePlayers()) {

            player.showPlayer(plugin, players);
            players.showPlayer(plugin, player);

        }

        if (config.getBoolean("settings.duel_server")) {

            player.teleport(config.getLocation("settings.spawn"));

        }

    }

}
