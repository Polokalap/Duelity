package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.GUI.SetupGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.Difficulty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetupCommand implements CommandExecutor, TabCompleter {

    private Main plugin = Main.getInstance();
    private FileConfiguration config = plugin.getConfig();

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

            case "start":

                PlayerCache.tempDifficulty.put(player, Difficulty.valueOf(config.getString("settings.difficulty")));

                new SetupGUI().openGUI(player);

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
                return List.of("start");

            default:
                return List.of();

        }

    }

}
