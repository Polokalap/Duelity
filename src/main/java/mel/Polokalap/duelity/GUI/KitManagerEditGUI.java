package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.KitUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class KitManagerEditGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("editor.name");
        size = 54;
        filler = false;
        holder = new EditorGUI();

        super.openGUI(player);

        ItemStack spacer = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();

        spacerMeta.setDisplayName(" ");

        spacerMeta.setHideTooltip(true);

        spacer.setItemMeta(spacerMeta);

        menu.setItem(5, spacer);

        for (int i = 9; i < 18; i++) menu.setItem(i, spacer);

        ItemStack importItem = new ItemStack(Material.CHEST);
        ItemMeta importMeta = importItem.getItemMeta();

        importMeta.setDisplayName(NewConfig.getString("editor.admin.import"));

        ItemUtil.assignPDC("editor_admin_import", importMeta);

        importItem.setItemMeta(importMeta);

        menu.setItem(6, importItem);

        ItemStack options = new ItemStack(Material.GRINDSTONE);
        ItemMeta optionsMeta = options.getItemMeta();

        optionsMeta.setDisplayName(NewConfig.getString("editor.admin.options"));

        ItemUtil.assignPDC("editor_admin_options", optionsMeta);

        options.setItemMeta(optionsMeta);

        menu.setItem(7, options);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();

        ItemUtil.assignPDC("admin_kit_item_editor_back_button", backMeta);

        backMeta.setDisplayName(NewConfig.getString("editor.admin.back"));

        back.setItemMeta(backMeta);

        menu.setItem(8, back);

        ConfigurationSection kitSection = KitUtil.getItems(PlayerCache.editingKit.get(player));

        List<?> items = kitSection.getList("items");
        ArrayList<ItemStack> hotbar = new ArrayList<>();
        int slot = 18;

        for (int i = 0; i < 36; i++) {

            Object obj = items.get(i);

            if (!(obj instanceof ItemStack item)) continue;

            if (i <= 8) {

                hotbar.add(item);

            } else {

                menu.setItem(slot++, item);

            }

        }

        for (ItemStack item : hotbar) {

            menu.setItem(slot++, item);

        }

        menu.setItem(0, (ItemStack) items.get(items.size() - 2));
        menu.setItem(1, (ItemStack) items.get(items.size() - 3));
        menu.setItem(2, (ItemStack) items.get(items.size() - 4));
        menu.setItem(3, (ItemStack) items.get(items.size() - 5));
        menu.setItem(4, (ItemStack) items.get(items.size() - 1));

    }

}
