package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.Managers.AddArenaManager;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LeaveCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(NewConfig.getString("console.player"));
            return true;

        }

        if (PlayerCache.editingArena.contains(player)) {

            AddArenaManager.leave(player);

        }

        if (PlayerCache.inDuel.containsKey(player)) {

            // TODO: Duel managerből kilépteti

        }

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        return List.of();

    }

}
