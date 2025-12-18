package mel.Polokalap.duelity.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.AddArenaManager;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import mel.Polokalap.duelity.Utils.WorldEdit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

            if (name.equals(NewConfig.getString("arenas.wand.name"))) {

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

                if (number >= 53) {

                    Sound.Error(player);
                    player.sendMessage(NewConfig.getString("arenas.world.too_many"));
                    return;

                }

                player.sendMessage(NewConfig.getString("arenas.world.save"));
                Sound.Click(player);

                WorldEdit.saveSchem(PlayerCache.regP.get(player), PlayerCache.regS.get(player), arenaName);

                ConfigurationSection arena = arenas.createSection(String.valueOf(number));

                arena.set("name", arenaName);

                arena.set("blue.x", (int) PlayerCache.arenaBlue.get(player).getX() - PlayerCache.regP.get(player).getX());
                arena.set("blue.y", (int) PlayerCache.arenaBlue.get(player).getY() - PlayerCache.regP.get(player).getY());
                arena.set("blue.z", (int) PlayerCache.arenaBlue.get(player).getZ() - PlayerCache.regP.get(player).getZ());

                arena.set("red.x", (int) PlayerCache.arenaRed.get(player).getX() - PlayerCache.regP.get(player).getX());
                arena.set("red.y", (int) PlayerCache.arenaRed.get(player).getY() - PlayerCache.regP.get(player).getY());
                arena.set("red.z", (int) PlayerCache.arenaRed.get(player).getZ() - PlayerCache.regP.get(player).getZ());

                arena.set("icon", item.getType().toString());
                plugin.saveArenaConfig();

                AddArenaManager.leave(player);

            }

        }

    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {

        Player player = event.getPlayer();
        String message = event.signedMessage().message();

//        if (settingSpawn.contains(player)) {
//
//            event.setCancelled(true);
//
//            if (message.equals("done")) {
//
//                settingSpawn.remove(player);
//                muteChat.remove(player);
//
//                Bukkit.getScheduler().runTask(plugin, () -> {
//
//                    Location loc = player.getLocation();
//
//                    player.sendActionBar(" ");
//                    new SetupGUI().openGUI(player);
//
//                    config.set("settings.spawn", loc);
//                    plugin.saveConfig();
//
//                });
//
//                return;
//
//            }
//
//            Sound.Error(player);
//            player.sendActionBar("§c" + NewConfig.getString("setup.spawn.action_bar"));
//
//        }

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

            if (name.equals(NewConfig.getString("arenas.wand.name"))) {

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

            if (name.equals(NewConfig.getString("arenas.wand.name"))) {

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
