package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.NewConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArenaManagerGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("kits.name");
        size = 54;

        super.openGUI(player);

        ItemStack addArena = new ItemStack(Material.OAK_STAIRS);
        ItemMeta addMeta = addArena.getItemMeta();

        addMeta.setDisplayName(NewConfig.getString("arenas.add.name"));

        addMeta.setLore(NewConfig.getStringList("arenas.add.lore"));

        addArena.setItemMeta(addMeta);

        menu.setItem(4, addArena);

        if (arenas.getList("arenas").isEmpty()) {

            ItemStack noArenas = new ItemStack(Material.BARRIER);
            ItemMeta noArenasMeta = noArenas.getItemMeta();

            noArenasMeta.setDisplayName(NewConfig.getString("arenas.no_arenas.name"));

            noArenasMeta.setLore(NewConfig.getStringList("arenas.no_arenas.lore"));

            noArenas.setItemMeta(noArenasMeta);

            menu.setItem(10, noArenas);

            return;

        }

    }

}
