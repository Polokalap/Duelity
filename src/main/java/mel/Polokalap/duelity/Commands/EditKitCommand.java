package mel.Polokalap.duelity.Commands;

import mel.Polokalap.duelity.GUI.PlayerEditKitGUI;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.KitEditorManager;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EditKitCommand implements CommandExecutor, TabCompleter {

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

        ArrayList<String> kitNames = new ArrayList<>();

        ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

        if (kitsSection == null) return true;

        for (String key : kitsSection.getKeys(false)) {

            kitNames.add(kits.getString("kits." + key + ".name").toLowerCase());

        }

        if (!kitNames.contains(args[0].toLowerCase())) {

            Sound.Error(player);
            player.sendMessage(NewConfig.getString("player.args"));
            return true;

        }

        PlayerCache.editingKit.put(player, args[0].toLowerCase());

        new PlayerEditKitGUI().openGUI(player);
        KitEditorManager.join(player);

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        ArrayList<String> kitNames = new ArrayList<>();

        ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

        if (kitsSection == null) return List.of();

        for (String key : kitsSection.getKeys(false)) {

            kitNames.add(kits.getString("kits." + key + ".name"));

        }

        switch (args.length) {

            case 1:
                return kitNames;

            default:
                return List.of();

        }

    }

}
