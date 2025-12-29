package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.GUI.KitsManagerGUI;
import mel.Polokalap.duelity.Listeners.GUIListener;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KitsCommand implements CommandExecutor, TabCompleter {

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

        new KitsManagerGUI().openGUI(player);

        PlayerCache.tempName.remove(player);
        PlayerCache.tempIcon.remove(player);
        PlayerCache.tempKit.remove(player);
        PlayerCache.tempHp.remove(player);
        PlayerCache.tempGamemode.remove(player);
        PlayerCache.selectedArenas.remove(player);
        PlayerCache.editingKit.remove(player);
        GUIListener.addingKit.remove(player);

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        return List.of();

    }

}
