package mel.Polokalap.duelity.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.AddArenaManager;
import mel.Polokalap.duelity.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class AddArenaListener implements Listener {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        String name = item.getItemMeta().getDisplayName();

        if (PlayerCache.editingArena.contains(player)) {

            if (ItemUtil.PDCHelper("arena_wand", item)) {

                event.setCancelled(true);
                Sound.Error(player);

            }

            if (PlayerCache.settingArenaIcon.contains(player)) {

                String arenaName = PlayerCache.arenaName.get(player);

                ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");
                if (arenas == null) {
                    arenas = plugin.getArenaConfig().createSection("arenas");
                }

                int number = arenas.getKeys(false).size();

                if (number >= 28) {

                    Sound.Error(player);
                    player.sendMessage(NewConfig.getString("arenas.world.too_many"));
                    return;

                }

                player.sendMessage(NewConfig.getString("arenas.world.save"));
                Sound.Click(player);

                WorldEdit.saveSchem(PlayerCache.regP.get(player), PlayerCache.regS.get(player), arenaName);

                ConfigurationSection arena = arenas.createSection(String.valueOf(number));

                arena.set("name", arenaName);

                Location origin1 = PlayerCache.regP.get(player);
                Location origin2 = PlayerCache.regS.get(player);
                Location blue = PlayerCache.arenaBlue.get(player);
                Location red  = PlayerCache.arenaRed.get(player);

                double minX = Math.min(origin1.getX(), origin2.getX());
                double minY = Math.min(origin1.getY(), origin2.getY());
                double minZ = Math.min(origin1.getZ(), origin2.getZ());

                Location corner = new Location(player.getWorld(), minX, minY, minZ);

                arena.set("blue.x", blue.getX() - corner.getX());
                arena.set("blue.y", blue.getY() - corner.getY());
                arena.set("blue.z", blue.getZ() - corner.getZ());

                arena.set("red.x", red.getX() - corner.getX());
                arena.set("red.y", red.getY() - corner.getY());
                arena.set("red.z", red.getZ() - corner.getZ());

                arena.set("icon", item.getType().toString());

                plugin.saveArenaConfig();

                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                        AddArenaManager.leave(player);

                }, 5L);

            }

        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.editingArena.contains(player)) {

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.editingArena.contains(player)) AddArenaManager.leave(player);

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.editingArena.contains(player)) {

            if (event.getBlock().getLocation().equals(new Location(player.getWorld(), 0.0d, 99.0d, 0.0d))) event.setCancelled(true);


            if (player.getInventory().getItemInMainHand().isEmpty()) return;

            String name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();

            if (ItemUtil.PDCHelper("arena_wand", player.getInventory().getItemInMainHand())) {

                event.setCancelled(true);

                if (PlayerCache.regP.containsKey(player) && PlayerCache.regP.get(player).equals(event.getBlock().getLocation())) return;

                PlayerCache.regP.put(player, event.getBlock().getLocation());
                player.sendMessage(NewConfig.getStringCompiled("arenas.wand.primary")
                        .replaceAll("ẞx", String.valueOf(event.getBlock().getLocation().getBlockX()))
                        .replaceAll("ẞy", String.valueOf(event.getBlock().getLocation().getBlockY()))
                        .replaceAll("ẞz", String.valueOf(event.getBlock().getLocation().getBlockZ()))
                );

            }

        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.editingArena.contains(player)) {

            if (player.getInventory().getItemInMainHand().isEmpty()) return;

            if (!event.hasBlock()) return;

            if (!event.getAction().isRightClick()) return;

            String name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();

            if (ItemUtil.PDCHelper("arena_wand", player.getInventory().getItemInMainHand())) {

                if (PlayerCache.regS.containsKey(player) && PlayerCache.regS.get(player).equals(event.getClickedBlock().getLocation())) return;

                PlayerCache.regS.put(player, event.getClickedBlock().getLocation());
                player.sendMessage(NewConfig.getStringCompiled("arenas.wand.secondary")
                        .replaceAll("ẞx", String.valueOf(event.getClickedBlock().getLocation().getBlockX()))
                        .replaceAll("ẞy", String.valueOf(event.getClickedBlock().getLocation().getBlockY()))
                        .replaceAll("ẞz", String.valueOf(event.getClickedBlock().getLocation().getBlockZ()))
                );

            }

        }

    }

}
