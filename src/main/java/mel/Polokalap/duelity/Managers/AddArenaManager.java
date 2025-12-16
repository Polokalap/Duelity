package mel.Polokalap.duelity.Managers;

import mel.Polokalap.duelity.Listeners.GUIListener;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AddArenaManager {

    public static void join(Player player) {

        PlayerCache.editingArena.add(player);

        player.setGameMode(GameMode.CREATIVE);
        new WorldUtil().makeEmptyWorld(player.getUniqueId() + "-arena_world", true);
        World world = Bukkit.getWorld(player.getUniqueId() + "-arena_world");

        player.teleport(new Location(world, 0, 100, 0));
        PlayerCache.playerWorld.put(player, world);

        GUIListener.muteChat.add(player);

    }

    public static void leave(Player player) {

        if (!PlayerCache.editingArena.contains(player)) return;

        PlayerCache.editingArena.remove(player);
        WorldUtil.deleteWorldByWorld(PlayerCache.playerWorld.get(player));
        PlayerCache.playerWorld.remove(player);

    }

}
