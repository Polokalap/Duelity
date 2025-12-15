package mel.Polokalap.duelity.GUI;

import com.destroystokyo.paper.profile.PlayerProfile;
import mel.Polokalap.duelity.Listeners.GUIListener;
import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class AddKitGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("kits.add_gui.name");
        if (
                GUIListener.tempName.get(player) != null &&
                        GUIListener.tempIcon.get(player) != null &&
                        GUIListener.tempKit.get(player) != null
        ) size = 36;
        else size = 27;

        super.openGUI(player);

        ItemStack nameItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta nameMeta = nameItem.getItemMeta();

        nameMeta.setDisplayName(NewConfig.getString("kits.add_gui.set_name.name"));

        if (GUIListener.tempName.get(player) != null)
            nameMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.set_name.set_lore").replaceAll("ẞname", GUIListener.tempName.get(player))));
        else nameMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.set_name.lore")));

        nameItem.setItemMeta(nameMeta);

        menu.setItem(10, nameItem);

        ItemStack iconItem;

        if (GUIListener.tempIcon.get(player) != null && GUIListener.tempIcon.get(player) != Material.AIR) iconItem = new ItemStack(GUIListener.tempIcon.get(player));
        else iconItem = new ItemStack(Material.DIAMOND_SWORD);

        ItemMeta iconMeta = iconItem.getItemMeta();

        iconMeta.setDisplayName(NewConfig.getString("kits.add_gui.set_icon.name"));

        iconMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.set_icon.lore")));

        iconItem.setItemMeta(iconMeta);

        menu.setItem(13, iconItem);

        ItemStack kitItem = new ItemStack(Material.CHEST);

        ItemMeta kitMeta = kitItem.getItemMeta();

        kitMeta.setDisplayName(NewConfig.getString("kits.add_gui.set_inventory.name"));

        kitMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.set_inventory.lore")));

        kitItem.setItemMeta(kitMeta);

        menu.setItem(16, kitItem);

        if (
                GUIListener.tempName.get(player) != null &&
                GUIListener.tempIcon.get(player) != null &&
                GUIListener.tempKit.get(player) != null
        ) {

            UUID ArrowUUID = UUID.fromString("523f3491-5336-48e6-98b2-b5e1593b9423");

            ItemStack nextPageItem = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta nextPageMeta = (SkullMeta) nextPageItem.getItemMeta();

            PlayerProfile profile = Bukkit.createProfile(ArrowUUID);
            nextPageMeta.setPlayerProfile(profile);

            nextPageMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.arrow.name"));

            nextPageMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.next.arrow.lore")));

            nextPageItem.setItemMeta(nextPageMeta);

            menu.setItem(35, nextPageItem);

        }

    }

}
