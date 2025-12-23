package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.GUI.ArenaManagerGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArenasCommand implements CommandExecutor, TabCompleter {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(NewConfig.getString("console.player"));
            return true;

        }

        if (!player.hasPermission("duelity.admin")) {

            player.sendMessage(NewConfig.getString("player.permission"));
            Sound.Error(player);
            return true;

        }

        if (PlayerCache.editingArena.contains(player)) {

            if (args.length < 2) {

                player.sendMessage(NewConfig.getString("player.args"));
                Sound.Error(player);
                return true;

            }

            switch (args[0].toLowerCase()) {

                case "save":

                    String on = NewConfig.getString("player.on");
                    String off = NewConfig.getString("player.off");

                    boolean blue = false;
                    boolean red = false;
                    boolean map = false;

                    if (PlayerCache.arenaBlue.containsKey(player)) blue = true;
                    if (PlayerCache.arenaRed.containsKey(player)) red = true;
                    if (PlayerCache.regP.containsKey(player) && PlayerCache.regS.containsKey(player)) map = true;

                    if (blue && red && map) {

                        if (args.length < 2) {

                            player.sendMessage(NewConfig.getString("arenas.world.set_name"));
                            Sound.Error(player);
                            return true;

                        }

                        boolean exists = false;

                        ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

                        for (String arenaId : arenas.getKeys(false)) {

                            ConfigurationSection arena = arenas.getConfigurationSection(arenaId);
                            if (arena.getString("name").equals(args[1])) exists = true;
                            PlayerCache.arenaNames.clear();
                            PlayerCache.arenaNames.add(arena.getString("name"));

                        }

                        if (exists) {

                            player.sendMessage(NewConfig.getString("arenas.world.name_exists"));
                            Sound.Error(player);
                            return true;

                        }

                        PlayerCache.arenaName.put(player, args[1]);
                        player.sendMessage(NewConfig.getString("arenas.world.no_icon"));
                        PlayerCache.settingArenaIcon.add(player);
                        Sound.Error(player);
                        return true;

                    } else {

                        player.sendMessage(NewConfig.getStringCompiled("arenas.world.check")
                                .replaceAll("ẞmap", map ? on : off)
                                .replaceAll("ẞblue", blue ? on : off)
                                .replaceAll("ẞred", red ? on : off)
                        );

                    }

                    break;

                case "wand":

                    switch (args[1]) {

                        case "give":

                            ItemStack wand = new ItemStack(Material.IRON_SHOVEL);

                            ItemMeta wandMeta = wand.getItemMeta();

                            wandMeta.setDisplayName(NewConfig.getString("arenas.wand.name"));
                            wandMeta.setLore(NewConfig.getStringList("arenas.wand.lore"));

                            wand.setItemMeta(wandMeta);

                            player.getInventory().addItem(wand);

                            break;

                        default:

                            player.sendMessage(NewConfig.getString("player.args"));
                            Sound.Error(player);

                            break;

                    }

                    break;

                case "set":

                    if (args.length < 1) {

                        player.sendMessage(NewConfig.getString("player.args"));
                        Sound.Error(player);

                    }

                    switch (args[1].toLowerCase()) {

                        case "blue":

                            player.sendMessage(NewConfig.getString("arenas.world.blue_saved"));
                            Sound.Click(player);
                            PlayerCache.arenaBlue.put(player, player.getLocation());

                            break;

                        case "red":

                            player.sendMessage(NewConfig.getString("arenas.world.red_saved"));
                            Sound.Click(player);
                            PlayerCache.arenaRed.put(player, player.getLocation());

                            break;

                        default:

                            player.sendMessage(NewConfig.getString("player.args"));
                            Sound.Error(player);

                            break;

                    }

                    break;

                default:

                    player.sendMessage(NewConfig.getString("player.args"));
                    Sound.Error(player);

                    break;
            }

            return true;

        }

        new ArenaManagerGUI().openGUI(player);

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (!sender.hasPermission("duelity.admin")) return List.of();

        if (PlayerCache.editingArena.contains(sender)) {

            switch (args.length) {

                case 1:
                    return List.of("save", "set", "wand");

                case 2:

                    switch (args[0].toLowerCase()) {

                        case "set":
                            return List.of("blue", "red");

                        case "wand":
                            return List.of("give");

                        default:
                            return List.of();

                    }

                default:
                    return List.of();

            }

        }

        return List.of();

    }

}
