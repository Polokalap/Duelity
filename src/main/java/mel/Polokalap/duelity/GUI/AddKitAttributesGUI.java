package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Listeners.GUIListener;
import mel.Polokalap.duelity.Utils.Gamemodes;
import mel.Polokalap.duelity.Utils.NewConfig;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;

public class AddKitAttributesGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("kits.add_gui.next.name");
        size = 27;

        super.openGUI(player);

        ItemStack healthItem = new ItemStack(Material.POTION);
        PotionMeta healthMeta = (PotionMeta) healthItem.getItemMeta();

        healthMeta.setColor(Color.fromRGB(230, 10, 10));

        healthMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.health.name").replaceAll("ẞhp", String.valueOf(GUIListener.tempHp.get(player))));

        healthMeta.setLore(NewConfig.getStringList("kits.add_gui.next.health.lore"));

        healthItem.setItemMeta(healthMeta);

        menu.setItem(10, healthItem);

        ItemStack gamemodeItem = new ItemStack(Material.JIGSAW);
        ItemMeta gamemodeMeta = gamemodeItem.getItemMeta();

        gamemodeMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.gamemode.name"));

        gamemodeMeta.setLore(List.of(
                NewConfig.getStringList("kits.add_gui.next.gamemode.lore").get(0).replaceAll("ẞa", GUIListener.tempGamemode.get(player) == Gamemodes.SURVIVAL ? "§a§u" : "§7"),
                NewConfig.getStringList("kits.add_gui.next.gamemode.lore").get(1).replaceAll("ẞb", GUIListener.tempGamemode.get(player) == Gamemodes.ADVENTURE ? "§a§u" : "§7")
        ));

        gamemodeItem.setItemMeta(gamemodeMeta);

        menu.setItem(13, gamemodeItem);

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);

        ItemMeta mapMeta = mapItem.getItemMeta();

        mapMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.map.name"));

        mapMeta.setLore(NewConfig.getStringList("kits.add_gui.next.map.lore"));

        mapItem.setItemMeta(mapMeta);

        menu.setItem(16, mapItem);

//        if (
//                GUIListener.tempName.get(player) != null &&
//                GUIListener.tempIcon.get(player) != null &&
//                GUIListener.tempKit.get(player) != null
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
//            nextPageMeta.setLore(List.of(NewConfig.getStringCompiled("kits.add_gui.next.arrow.lore")));
//
//            nextPageItem.setItemMeta(nextPageMeta);
//
//            menu.setItem(35, nextPageItem);
//
//        }

    }

}
