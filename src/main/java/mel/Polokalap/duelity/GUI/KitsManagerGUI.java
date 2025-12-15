package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.NewConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KitsManagerGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("kits.name");
        size = 54;

        super.openGUI(player);

        ItemStack addKit = new ItemStack(Material.ENDER_EYE);
        ItemMeta addMeta = addKit.getItemMeta();

        addMeta.setDisplayName(NewConfig.getString("kits.add.name"));

        addMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add.lore")));

        addKit.setItemMeta(addMeta);

        menu.setItem(4, addKit);

        if (kits.getList("kits").isEmpty()) {

            ItemStack noKits = new ItemStack(Material.BARRIER);
            ItemMeta noKitsMeta = noKits.getItemMeta();

            noKitsMeta.setDisplayName(NewConfig.getString("kits.no_kits.name"));

            noKitsMeta.setLore(List.of(NewConfig.getStringCompiled("kits.no_kits.lore")));

            noKits.setItemMeta(noKitsMeta);

            menu.setItem(10, noKits);

            return;

        }

    }

}
