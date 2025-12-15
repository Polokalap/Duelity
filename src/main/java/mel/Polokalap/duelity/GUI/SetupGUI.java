package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.NewConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SetupGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("setup.name");
        size = 27;

        super.openGUI(player);

        ItemStack duelServer = new ItemStack(Material.IRON_AXE);
        ItemMeta duelMeta = duelServer.getItemMeta();

        duelMeta.setDisplayName(NewConfig.getString("setup.duel.name"));

        duelMeta.setLore(List.of(NewConfig.getStringCompiled("setup.duel.lore").replaceAll("ẞanswer", config.getBoolean("settings.duel_server") ? NewConfig.getString("player.y") : NewConfig.getString("player.n"))));

        duelServer.setItemMeta(duelMeta);

        menu.setItem(11, duelServer);

        ItemStack spawnItem = new ItemStack(Material.CLOCK);
        ItemMeta spawnMeta = spawnItem.getItemMeta();

        spawnMeta.setDisplayName(NewConfig.getString("setup.spawn.name"));

        spawnMeta.setLore(NewConfig.getStringList("setup.spawn.lore"));

        spawnItem.setItemMeta(spawnMeta);

        menu.setItem(13, spawnItem);

        ItemStack saveItem = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta saveMeta = saveItem.getItemMeta();

        saveMeta.setDisplayName(NewConfig.getString("setup.save.name"));

        saveMeta.setLore(NewConfig.getStringList("setup.save.lore"));

        saveItem.setItemMeta(saveMeta);

        menu.setItem(15, saveItem);

    }

}
