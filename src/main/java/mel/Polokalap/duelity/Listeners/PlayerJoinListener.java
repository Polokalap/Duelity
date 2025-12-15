package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.Managers.PlayerManager;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.ServerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (!ServerUtil.isServerSetupDone()) {

            if (player.hasPermission("duelity.admin")) {

                 player.sendMessage(NewConfig.getStringCompiled("admin.setup_incomplete"));

            }

        } else {

            PlayerManager.load(player);

        }

    }

}
