package mel.Polokalap.duelity.GUI;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

        menu.setItem(13, iconItem);

        ItemStack kitItem = new ItemStack(Material.CHEST);

        ItemMeta kitMeta = kitItem.getItemMeta();

        kitMeta.setDisplayName(NewConfig.getString("kits.add_gui.set_inventory.name"));

        ItemUtil.assignPDC("kits_add_gui_set_inventory_name", kitMeta);

        kitMeta.setLore(NewConfig.getStringList("kits.add_gui.set_inventory.lore"));

        kitItem.setItemMeta(kitMeta);

        menu.setItem(16, kitItem);

        if (
                PlayerCache.tempName.get(player) != null &&
                PlayerCache.tempIcon.get(player) != null &&
                PlayerCache.tempKit.get(player) != null
        ) {

            ItemStack nextPageItem = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta nextPageMeta = (SkullMeta) nextPageItem.getItemMeta();

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", config.getString("kits.add_gui.next.arrow.skin")));
            nextPageMeta.setPlayerProfile(profile);

            nextPageMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.arrow.name"));

            ItemUtil.assignPDC("kits_add_gui_next_arrow_name", nextPageMeta);

            nextPageMeta.setLore(NewConfig.getStringList("kits.add_gui.next.arrow.lore"));

            nextPageItem.setItemMeta(nextPageMeta);

            menu.setItem(26, nextPageItem);

        }

    }

}
