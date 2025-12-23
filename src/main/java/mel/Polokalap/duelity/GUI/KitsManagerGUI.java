package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
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

        ItemUtil.assignPDC("kits_add_name", addMeta);

        addMeta.setLore(NewConfig.getStringList("kits.add.lore"));

        addKit.setItemMeta(addMeta);

        menu.setItem(4, addKit);

        ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

        if (kitsSection == null || kitsSection.getKeys(false).isEmpty()) {

            ItemStack noKitsItem = new ItemStack(Material.BARRIER);
            ItemMeta noKitsMeta = noKitsItem.getItemMeta();

            noKitsMeta.setDisplayName(NewConfig.getString("kits.no_kits.name"));

            ItemUtil.assignPDC("kits_no_kits_name", noKitsMeta);

            noKitsMeta.setLore(NewConfig.getStringList("kits.no_kits.lore"));

            noKitsItem.setItemMeta(noKitsMeta);

            menu.setItem(10, noKitsItem);

            return;

        }

        HashMap<String, Material> kitsList = new HashMap<>();

        int slot = 10;

        for (String kitId : kitsSection.getKeys(false)) {

            ConfigurationSection kit = kitsSection.getConfigurationSection(kitId);

            if (
                    slot == 17 ||
                            slot == 26 ||
                            slot == 35 ||
                            slot == 44
            ) slot += 2;

            String name = kit.getString("name");
            Material icon = Material.valueOf(kit.getString("icon"));

            kitsList.put(name, icon);

            ItemStack kitIcon = new ItemStack(icon);
            ItemMeta kitMeta = kitIcon.getItemMeta();

            kitMeta.setDisplayName(NewConfig.getString("kits.kit.color") + name);

            ItemUtil.assignPDC("kit-" + name, kitMeta);

            kitMeta.setLore(NewConfig.getStringList("kits.kit.lore"));

            kitIcon.setItemMeta(kitMeta);

            menu.setItem(slot++, kitIcon);

        }

    }

}
