package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminEditKitGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("editor.name");
        size = 27;

        super.openGUI(player);

        ItemStack nameKit = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta nameMeta = nameKit.getItemMeta();

        nameMeta.setDisplayName(NewConfig.getString("editor.edit.edit_name.name"));

        ItemUtil.assignPDC("editor_edit_edit_name_name", nameMeta);

        nameMeta.setLore(NewConfig.getStringList("editor.edit.edit_name.lore"));

        nameKit.setItemMeta(nameMeta);

        menu.setItem(10, nameKit);

        ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

        Material material = Material.valueOf((String) kit.get("icon"));

        ItemStack iconKit = new ItemStack(material);
        ItemMeta iconMeta = iconKit.getItemMeta();

        iconMeta.setDisplayName(NewConfig.getString("editor.edit.edit_icon.name"));

        ItemUtil.assignPDC("editor_edit_edit_icon_name", iconMeta);

        iconMeta.setLore(NewConfig.getStringList("editor.edit.edit_icon.lore"));

        iconKit.setItemMeta(iconMeta);

        menu.setItem(11, iconKit);

        ItemStack healthKit = new ItemStack(Material.POTION);
        PotionMeta healthMeta = (PotionMeta) healthKit.getItemMeta();

        healthMeta.setColor(Color.fromRGB(230, 10, 10));

        ItemUtil.assignPDC("admin_edit_kit_gui_health", healthMeta);

        healthMeta.setDisplayName(NewConfig.getString("editor.edit.health.name").replaceAll("ẞhp", String.valueOf(PlayerCache.tempHp.get(player))));

        healthMeta.setLore(NewConfig.getStringList("editor.edit.health.lore"));

        healthKit.setItemMeta(healthMeta);

        menu.setItem(12, healthKit);

        ItemStack gamemodeItem = new ItemStack(Material.JIGSAW);
        ItemMeta gamemodeMeta = gamemodeItem.getItemMeta();

        gamemodeMeta.setDisplayName(NewConfig.getString("editor.edit.gamemode.name"));

        ItemUtil.assignPDC("admin_edit_kit_gui_gamemode", gamemodeMeta);

        gamemodeMeta.setLore(List.of(
                NewConfig.getStringList("editor.edit.gamemode.lore").get(0).replaceAll("ẞa", Gamemodes.valueOf((String) kit.get("gamemode")) == Gamemodes.SURVIVAL ? "§a§u" : "§7"),
                NewConfig.getStringList("editor.edit.gamemode.lore").get(1).replaceAll("ẞb", Gamemodes.valueOf((String) kit.get("gamemode")) == Gamemodes.ADVENTURE ? "§a§u" : "§7")
        ));

        gamemodeItem.setItemMeta(gamemodeMeta);

        menu.setItem(13, gamemodeItem);

        PlayerCache.selectedArenas.put(player, (ArrayList<String>) kit.getList("arenas"));

        ItemStack mapKit = new ItemStack(Material.FILLED_MAP);
        ItemMeta mapMeta = mapKit.getItemMeta();

        mapMeta.setDisplayName(NewConfig.getString("editor.edit.map.name"));

        ItemUtil.assignPDC("editor_edit_map_name", mapMeta);

        mapMeta.setLore(NewConfig.getStringList("editor.edit.map.lore"));

        mapKit.setItemMeta(mapMeta);

        menu.setItem(14, mapKit);

        ItemStack regenItem;
        if (PlayerCache.tempRegen.get(player)) regenItem = new ItemStack(Material.COOKED_BEEF);
        else regenItem = new ItemStack(Material.ROTTEN_FLESH);
        ItemMeta regenMeta = regenItem.getItemMeta();

        regenMeta.setDisplayName(NewConfig.getString("kits.add_gui.regen.name"));

        ItemUtil.assignPDC("kits_add_gui_regen_name", regenMeta);

        regenMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.regen.lore").replaceAll("ẞanswer", PlayerCache.tempRegen.get(player) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

        regenItem.setItemMeta(regenMeta);

        menu.setItem(15, regenItem);

        ItemStack deleteKit = new ItemStack(Material.BARRIER);
        ItemMeta deleteMeta = deleteKit.getItemMeta();

        deleteMeta.setDisplayName(NewConfig.getString("editor.edit.delete.name"));

        ItemUtil.assignPDC("editor_edit_delete_name", deleteMeta);

        deleteMeta.setLore(NewConfig.getStringList("editor.edit.delete.lore"));

        deleteKit.setItemMeta(deleteMeta);

        menu.setItem(16, deleteKit);

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();

        ItemUtil.assignPDC("admin_kit_editor_attributes_back", backMeta);

        backMeta.setDisplayName(NewConfig.getString("editor.admin.back"));

        backButton.setItemMeta(backMeta);

        menu.setItem(26, backButton);

    }

}
