package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.GUI.DuelGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

public class SpectateCommand implements CommandExecutor, TabCompleter {

    Main plugin = Main.getInstance();
    FileConfiguration config = plugin.getConfig();
    FileConfiguration kits = plugin.getKitConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(NewConfig.getString("console.player"));
            return true;

        }

        if (args.length < 1) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("player.args"));
            return true;

        }

        Player toSpectate = Bukkit.getPlayerExact(args[0]);

        if (toSpectate == null || !toSpectate.isOnline()) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("spectate.offline").replaceAll("%player%", args[0]));
            return true;

        }

        if (toSpectate.getUniqueId().equals(player.getUniqueId())) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("spectate.self"));
            return true;

        }

        if (!PlayerCache.inDuel.contains(toSpectate)) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("spectate.not_in_duel").replaceAll("%player%", toSpectate.getName()));
            return true;

        }

        if (PlayerCache.duelSpectators.get(toSpectate)) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("spectate.disabled").replaceAll("%player%", toSpectate.getName()));
            return true;

        }

        PlayerCache.spectatePreGameMode.put(player, player.getGameMode());
        PlayerCache.spectatePreLocation.put(player, player.getLocation());
        PlayerCache.spectating.add(player);
        PlayerCache.spectatingPlayer.put(player, toSpectate);

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(toSpectate);

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
