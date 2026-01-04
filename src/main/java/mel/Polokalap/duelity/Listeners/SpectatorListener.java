package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SpectatorListener implements Listener {

    @EventHandler
    public void onCollideWithWall(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.spectating.contains(player)) return;

        player.setFlySpeed(0.1f);

        if (!event.hasChangedPosition()) return;

        Location to = event.getTo();
        Block head = to.clone().add(0, 1, 0).getBlock();

        if (head.getLocation().clone().getBlock().hasMetadata("player_placed")) return;

        if (isSolid(head)) event.setCancelled(true);

    }

    private boolean isSolid(Block block) {

        Material type = block.getType();

        return type.isSolid()
                && type != Material.COBWEB
                && type != Material.WATER
                && type != Material.LAVA;

    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {

        Player player = event.getPlayer();

        if (!PlayerCache.spectating.contains(player)) return;

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        event.setCancelled(true);

    }

}
