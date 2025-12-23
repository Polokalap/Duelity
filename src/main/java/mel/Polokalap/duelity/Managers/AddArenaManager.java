package mel.Polokalap.duelity.Managers;

import mel.Polokalap.duelity.Listeners.GUIListener;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.WorldUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddArenaManager {

    public static void join(Player player) {

        PlayerCache.editingArena.add(player);

        player.setGameMode(GameMode.CREATIVE);
        new WorldUtil().makeEmptyWorld(player.getUniqueId() + "-arena_world", true);
        World world = Bukkit.getWorld(player.getUniqueId() + "-arena_world");

        player.teleport(new Location(world, 0, 100, 0));
        PlayerCache.playerWorld.put(player, world);

        GUIListener.muteChat.add(player);

        player.getInventory().clear();

        ItemStack wand = new ItemStack(Material.IRON_SHOVEL);

        ItemMeta wandMeta = wand.getItemMeta();

        wandMeta.setDisplayName(NewConfig.getString("arenas.wand.name"));
        wandMeta.setLore(NewConfig.getStringList("arenas.wand.lore"));

        wand.setItemMeta(wandMeta);

        player.getInventory().addItem(wand);

        player.sendMessage(NewConfig.getString("arenas.world.join"));

    }

    public static void leave(Player player) {

        if (!PlayerCache.editingArena.contains(player)) return;

        PlayerCache.editingArena.remove(player);
        WorldUtil.deleteWorldByWorld(PlayerCache.playerWorld.get(player));
        PlayerCache.worlds.remove(PlayerCache.playerWorld.get(player));
        PlayerCache.playerWorld.remove(player);
        GUIListener.muteChat.remove(player);
        PlayerCache.arenaName.remove(player);
        PlayerCache.arenaBlue.remove(player);
        PlayerCache.arenaRed.remove(player);
        PlayerCache.editingArena.remove(player);
        PlayerCache.regP.remove(player);
        PlayerCache.regS.remove(player);
        player.getInventory().clear();

        player.setGameMode(GameMode.SURVIVAL);

        player.sendMessage(NewConfig.getString("arenas.world.quit"));

    }

}
