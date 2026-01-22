package mel.Polokalap.duelity.GUI;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import mel.Polokalap.duelity.Utils.Gamemodes;
import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class AddKitGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("kits.add_gui.name");
        size = 27;

        super.openGUI(player);

        ItemStack nameItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta nameMeta = nameItem.getItemMeta();

        nameMeta.setDisplayName(NewConfig.getString("kits.add_gui.set_name.name"));

        ItemUtil.assignPDC("kits_add_gui_set_name_name", nameMeta);

        if (PlayerCache.tempName.get(player) != null)
            nameMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.set_name.set_lore").replaceAll("ẞname", PlayerCache.tempName.get(player))));
        else nameMeta.setLore(NewConfig.getStringList("kits.add_gui.set_name.lore"));

        nameItem.setItemMeta(nameMeta);

        menu.setItem(10, nameItem);

        ItemStack iconItem;

        if (PlayerCache.tempIcon.get(player) != null && PlayerCache.tempIcon.get(player) != Material.AIR) iconItem = new ItemStack(PlayerCache.tempIcon.get(player));
        else iconItem = new ItemStack(Material.DIAMOND_SWORD);

        ItemMeta iconMeta = iconItem.getItemMeta();

        iconMeta.setDisplayName(NewConfig.getString("kits.add_gui.set_icon.name"));

        ItemUtil.assignPDC("kits_add_gui_set_icon_name", iconMeta);

        iconMeta.setLore(NewConfig.getStringList("kits.add_gui.set_icon.lore"));

        iconItem.setItemMeta(iconMeta);

        menu.setItem(11, iconItem);

        ItemStack kitItem = new ItemStack(Material.CHEST);

        ItemMeta kitMeta = kitItem.getItemMeta();

        kitMeta.setDisplayName(NewConfig.getString("kits.add_gui.set_inventory.name"));

        ItemUtil.assignPDC("kits_add_gui_set_inventory_name", kitMeta);

        kitMeta.setLore(NewConfig.getStringList("kits.add_gui.set_inventory.lore"));

        kitItem.setItemMeta(kitMeta);

        menu.setItem(12, kitItem);

        ItemStack healthItem = new ItemStack(Material.POTION);
        PotionMeta healthMeta = (PotionMeta) healthItem.getItemMeta();

        healthMeta.setColor(Color.fromRGB(230, 10, 10));

        healthMeta.setDisplayName(NewConfig.getString("kits.add_gui.health.name").replaceAll("ẞhp", String.valueOf(PlayerCache.tempHp.get(player))));

        ItemUtil.assignPDC("kits_add_gui_next_health_name", healthMeta);

        healthMeta.setLore(NewConfig.getStringList("kits.add_gui.health.lore"));

        healthItem.setItemMeta(healthMeta);

        menu.setItem(13, healthItem);

        ItemStack gamemodeItem = new ItemStack(Material.JIGSAW);
        ItemMeta gamemodeMeta = gamemodeItem.getItemMeta();

        gamemodeMeta.setDisplayName(NewConfig.getString("kits.add_gui.gamemode.name"));

        ItemUtil.assignPDC("kits_add_gui_next_gamemode_name", gamemodeMeta);

        gamemodeMeta.setLore(List.of(
                NewConfig.getStringList("kits.add_gui.gamemode.lore").get(0).replaceAll("ẞa", PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL ? "§a§u" : "§7"),
                NewConfig.getStringList("kits.add_gui.gamemode.lore").get(1).replaceAll("ẞb", PlayerCache.tempGamemode.get(player) == Gamemodes.ADVENTURE ? "§a§u" : "§7")
        ));

        gamemodeItem.setItemMeta(gamemodeMeta);

        menu.setItem(14, gamemodeItem);

        ItemStack regenItem;
        if (PlayerCache.tempRegen.get(player)) regenItem = new ItemStack(Material.COOKED_BEEF);
        else regenItem = new ItemStack(Material.ROTTEN_FLESH);
        ItemMeta regenMeta = regenItem.getItemMeta();

        regenMeta.setDisplayName(NewConfig.getString("kits.add_gui.regen.name"));

        ItemUtil.assignPDC("kits_add_gui_regen_name", regenMeta);

        regenMeta.setLore(List.of(NewConfig.getString("kits.add_gui.regen.lore").replaceAll("ẞanswer", PlayerCache.tempRegen.get(player) ? NewConfig.getString("player.on") : NewConfig.getString("player.off"))));

        regenItem.setItemMeta(regenMeta);

        menu.setItem(15, regenItem);

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);

        ItemMeta mapMeta = mapItem.getItemMeta();

        mapMeta.setDisplayName(NewConfig.getString("kits.add_gui.map.name"));

        ItemUtil.assignPDC("kits_add_gui_next_map_name", mapMeta);

        mapMeta.setLore(NewConfig.getStringList("kits.add_gui.map.lore"));

        mapItem.setItemMeta(mapMeta);

        menu.setItem(16, mapItem);

        if (
                PlayerCache.tempName.get(player) != null &&
                PlayerCache.tempIcon.get(player) != null &&
                PlayerCache.tempKit.get(player) != null &&
                PlayerCache.tempName.get(player) != null &&
                PlayerCache.selectedArenas.get(player) != null
        ) {

            ItemStack saveItem = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta saveMeta = (SkullMeta) saveItem.getItemMeta();

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", config.getString("kits.save.skin")));
            saveMeta.setPlayerProfile(profile);

            saveMeta.setDisplayName(NewConfig.getString("kits.save.name"));

            ItemUtil.assignPDC("kits_save_name", saveMeta);

            saveMeta.setLore(List.of(NewConfig.getStringCompiled("kits.save.lore")));

            saveItem.setItemMeta(saveMeta);

            menu.setItem(26, saveItem);

        }

//        if (
//                PlayerCache.tempName.get(player) != null &&
//                PlayerCache.tempIcon.get(player) != null &&
//                PlayerCache.tempKit.get(player) != null
//        ) {
//
//            ItemStack nextPageItem = new ItemStack(Material.PLAYER_HEAD);
//
//            SkullMeta nextPageMeta = (SkullMeta) nextPageItem.getItemMeta();
//
//            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
//            profile.setProperty(new ProfileProperty("textures", config.getString("kits.add_gui.next.arrow.skin")));
//            nextPageMeta.setPlayerProfile(profile);
//
//            nextPageMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.arrow.name"));
//
//            ItemUtil.assignPDC("kits_add_gui_next_arrow_name", nextPageMeta);
//
//            nextPageMeta.setLore(NewConfig.getStringList("kits.add_gui.next.arrow.lore"));
//
//            nextPageItem.setItemMeta(nextPageMeta);
//
//            menu.setItem(26, nextPageItem);
//
//        }

    }

}
