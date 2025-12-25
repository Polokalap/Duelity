package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.KitUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerEditKitGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("editor.name");
        size = 54;
        filler = false;
        holder = new PlayerGUI();

        super.openGUI(player);

        ItemStack spacer = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();

        spacerMeta.setDisplayName(" ");

        ItemUtil.assignPDC("unmoveable", spacerMeta);

        spacerMeta.setHideTooltip(true);

        spacer.setItemMeta(spacerMeta);

        menu.setItem(4, spacer);
        menu.setItem(6, spacer);

        for (int i = 9; i < 18; i++) menu.setItem(i, spacer);

        ItemStack resetItem = new ItemStack(Material.ORANGE_DYE);
        ItemMeta resetMeta = resetItem.getItemMeta();

        resetMeta.setDisplayName(NewConfig.getString("editor.player.reset"));

        ItemUtil.assignPDC("player_kit_editor_reset", resetMeta);

        resetItem.setItemMeta(resetMeta);

        menu.setItem(7, resetItem);

        ItemStack saveItem = new ItemStack(Material.LIME_DYE);
        ItemMeta saveMeta = saveItem.getItemMeta();

        saveMeta.setDisplayName(NewConfig.getString("editor.player.save"));

        ItemUtil.assignPDC("player_kit_editor_save", saveMeta);

        saveItem.setItemMeta(saveMeta);

        menu.setItem(8, saveItem);

        ConfigurationSection kitSection = KitUtil.getPlayerItems(PlayerCache.editingKit.get(player), player);

        List<?> items = kitSection.getList("items");
        ArrayList<ItemStack> hotbar = new ArrayList<>();
        int slot = 18;

        for (int i = 0; i < 36; i++) {

            Object obj = items.get(i);

            if (!(obj instanceof ItemStack item)) continue;

            if (item.getType() != Material.AIR) {

                ItemMeta meta = item.getItemMeta();

                ItemUtil.assignPDC("player_kit_editor_item-" + UUID.randomUUID(), meta);

                item.setItemMeta(meta);

            }

            if (i <= 8) {

                hotbar.add(item);

            } else {

                menu.setItem(slot++, item);

            }

        }

        for (ItemStack item : hotbar) {

            menu.setItem(slot++, item);

        }

        ItemStack helmet = (ItemStack) items.get(items.size() - 2);
        ItemStack chestplate = (ItemStack) items.get(items.size() - 3);
        ItemStack leggings = (ItemStack) items.get(items.size() - 4);
        ItemStack boots = (ItemStack) items.get(items.size() - 5);
        ItemStack offhand = (ItemStack) items.get(items.size() - 1);

        helmet = (helmet == null) ? new ItemStack(Material.AIR) : helmet;
        ItemMeta helmetMeta = helmet.getItemMeta();
        if (helmetMeta != null) {
            ItemUtil.assignPDC("player_kit_editor_save-" + UUID.randomUUID(), helmetMeta);
            helmet.setItemMeta(helmetMeta);
        }

        chestplate = (chestplate == null) ? new ItemStack(Material.AIR) : chestplate;
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        if (chestplateMeta != null) {
            ItemUtil.assignPDC("player_kit_editor_save-" + UUID.randomUUID(), chestplateMeta);
            chestplate.setItemMeta(chestplateMeta);
        }

        leggings = (leggings == null) ? new ItemStack(Material.AIR) : leggings;
        ItemMeta leggingsMeta = leggings.getItemMeta();
        if (leggingsMeta != null) {
            ItemUtil.assignPDC("player_kit_editor_save-" + UUID.randomUUID(), leggingsMeta);
            leggings.setItemMeta(leggingsMeta);
        }

        boots = (boots == null) ? new ItemStack(Material.AIR) : boots;
        ItemMeta bootsMeta = boots.getItemMeta();
        if (bootsMeta != null) {
            ItemUtil.assignPDC("player_kit_editor_save-" + UUID.randomUUID(), bootsMeta);
            boots.setItemMeta(bootsMeta);
        }

        offhand = (offhand == null) ? new ItemStack(Material.AIR) : offhand;
        ItemMeta offhandMeta = offhand.getItemMeta();
        if (offhandMeta != null) {
            ItemUtil.assignPDC("player_kit_editor_save-" + UUID.randomUUID(), offhandMeta);
            offhand.setItemMeta(offhandMeta);
        }

        menu.setItem(0, helmet);
        menu.setItem(1, chestplate);
        menu.setItem(2, leggings);
        menu.setItem(3, boots);
        menu.setItem(5, offhand);

    }

}
