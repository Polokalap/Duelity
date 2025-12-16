package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.Sound;
import mel.Polokalap.duelity.Utils.WorldEdit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorldEditTestCommand implements CommandExecutor, TabCompleter {

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

        if (args.length == 0) {

            player.sendMessage(NewConfig.getString("player.args"));
            return true;

        }

        switch (args[0].toLowerCase()) {

            case "save":

                WorldEdit.saveSchem(new Location(player.getWorld(), player.getLocation().getX() - 30, player.getLocation().getY() + 30, player.getLocation().getZ() - 30), new Location(player.getWorld(), player.getLocation().getX() + 30, player.getLocation().getY() - 30, player.getLocation().getZ() + 30), "test");

                break;

            case "place":

                WorldEdit.placeSchem(new Location(player.getWorld(), player.getLocation().getX() - 5, player.getLocation().getY() + 5, player.getLocation().getZ() - 5), "test");

                break;

            default:

                player.sendMessage(NewConfig.getString("player.args"));

                break;

        }

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (!sender.hasPermission("duelity.admin")) return List.of();

        switch (args.length) {

            case 1:
                return List.of("save", "place");

            default:
                return List.of();

        }

    }

}
