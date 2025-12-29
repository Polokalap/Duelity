package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.GUI.DuelGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DuelCommand implements CommandExecutor, TabCompleter {

    Main plugin = Main.getInstance();
    FileConfiguration config = plugin.getConfig();
    FileConfiguration kits = plugin.getKitConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(NewConfig.getString("console.player"));
            return true;

        }

        if (PlayerCache.inDuel.contains(player)) return true;

        if (args.length < 1) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("player.args"));
            return true;

        }

        Player opponent = Bukkit.getPlayerExact(args[0]);

        if (opponent == null || !opponent.isOnline()) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("duel.offline").replaceAll("%player%", args[0]));
            return true;

        }

        if (opponent.getUniqueId().equals(player.getUniqueId())) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("duel.self"));
            return true;

        }

        if (PlayerCache.inDuel.contains(opponent) || PlayerCache.preInDuel.contains(opponent)) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("duel.in_duel").replaceAll("%player%", opponent.getName()));
            return true;

        }

        if (PlayerCache.duelRequests.get(player) != null && PlayerCache.duelRequests.get(player).containsKey(opponent)) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("duel.sent"));
            return true;

        }

        if (PlayerCache.spectating.contains(player)) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("spectate.in_spectator").replaceAll("%player%", opponent.getName()));
            return true;

        }

        PlayerCache.duelOpponent.put(player, opponent);

        new DuelGUI().openGUI(player);

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        List<String> players = new ArrayList<>(List.of());

        for (Player player : Bukkit.getOnlinePlayers()) players.add(player.getName());

        switch (args.length) {

            case 1:
                return players;

            default:
                return List.of();

        }

    }

}
