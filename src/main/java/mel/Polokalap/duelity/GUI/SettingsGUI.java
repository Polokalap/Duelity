package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SettingsGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("settings_menu.name");
        size = 27;

        super.openGUI(player);

        ItemStack duelServer = new ItemStack(Material.IRON_AXE);
        ItemMeta duelMeta = duelServer.getItemMeta();

        duelMeta.setDisplayName(NewConfig.getString("settings_menu.duel.name"));

        ItemUtil.assignPDC("settings_menu_duel_name", duelMeta);

        duelMeta.setLore(List.of(NewConfig.getStringCompiled("settings_menu.duel.lore").replaceAll("ẞanswer", config.getBoolean("settings.duel_server") ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

        duelServer.setItemMeta(duelMeta);

        menu.setItem(10, duelServer);

        ItemStack spawnItem = new ItemStack(Material.ENDER_EYE);
        ItemMeta spawnMeta = spawnItem.getItemMeta();

        spawnMeta.setDisplayName(NewConfig.getString("settings_menu.spawn.name"));

        ItemUtil.assignPDC("settings_menu_spawn_name", spawnMeta);

        spawnMeta.setLore(NewConfig.getStringList("settings_menu.spawn.lore"));

        spawnItem.setItemMeta(spawnMeta);

        menu.setItem(11, spawnItem);

        ItemStack difficultyItem = new ItemStack(Material.FEATHER);
        ItemMeta difficultyMeta = difficultyItem.getItemMeta();

        difficultyMeta.setDisplayName(NewConfig.getString("settings_menu.difficulty.name"));

        ItemUtil.assignPDC("settings_menu_difficulty_name", difficultyMeta);

        difficultyMeta.setLore(List.of(
                NewConfig.getStringList("settings_menu.difficulties").get(0).replaceAll("ẞa", PlayerCache.tempDifficulty.get(player) == Difficulty.EASY ? "§a§u" : "§7"),
                NewConfig.getStringList("settings_menu.difficulties").get(1).replaceAll("ẞb", PlayerCache.tempDifficulty.get(player) == Difficulty.NORMAL ? "§a§u" : "§7"),
                NewConfig.getStringList("settings_menu.difficulties").get(2).replaceAll("ẞc", PlayerCache.tempDifficulty.get(player) == Difficulty.HARD ? "§a§u" : "§7")
        ));

        difficultyItem.setItemMeta(difficultyMeta);

        menu.setItem(12, difficultyItem);

        ItemStack skipDuels = new ItemStack(Material.CLOCK);
        ItemMeta skipMeta = skipDuels.getItemMeta();

        skipMeta.setDisplayName(NewConfig.getString("settings_menu.skip.name"));

        ItemUtil.assignPDC("settings_menu_skip_name", skipMeta);

        skipMeta.setLore(List.of(NewConfig.getStringCompiled("settings_menu.skip.lore").replaceAll("ẞanswer", config.getBoolean("settings.skip") ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

        skipDuels.setItemMeta(skipMeta);

        menu.setItem(13, skipDuels);

    }

}
