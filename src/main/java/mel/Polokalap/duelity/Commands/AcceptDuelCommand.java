package mel.Polokalap.duelity.Commands;

import io.papermc.paper.entity.LookAnchor;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.DuelManager;
import mel.Polokalap.duelity.Utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class AcceptDuelCommand implements CommandExecutor, TabCompleter {

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

        if (opponent.getUniqueId().equals(player.getUniqueId())) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("duel.self"));
            return true;

        }

        if (PlayerCache.inDuel.contains(player)) return true;

        if (PlayerCache.spectating.contains(player)) return true;

        if (
                PlayerCache.duelRequests.get(opponent) != null && PlayerCache.duelRequests.get(opponent).containsKey(player) ||
                PlayerCache.duelOpponent.get(opponent) != null && PlayerCache.duelOpponent.get(opponent).equals(player)
        ) {

            String kitName = PlayerCache.duelRequests.get(opponent).get(player);
            ConfigurationSection kit = KitUtil.getItems(kitName);
            ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

            Duel duel = new Duel(
                    opponent,
                    player,
                    kitName,
                    kit,
                    arenas,
                    plugin.getPlayerConfig().getInt("players." + opponent.getUniqueId() + ".settings.rounds", 3),
                    plugin.getPlayerConfig().getBoolean("players." + opponent.getUniqueId() + ".settings.spectate", true)
            );

            duel.startDuel();

            PlayerCache.duels.add(duel);

            PlayerCache.playerDuel.put(player, duel);
            PlayerCache.playerDuel.put(opponent, duel);

            return true;

        }

        Sound.Error(player);
        player.sendMessage(NewConfig.getString("duel.no_sent_invite"));

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
