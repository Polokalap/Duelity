package mel.Polokalap.duelity.GUI;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import mel.Polokalap.duelity.Listeners.GUIListener;
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

import java.util.List;
import java.util.UUID;

public class AddKitAttributesGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("kits.add_gui.next.name");
        size = 27;

        super.openGUI(player);

        ItemStack healthItem = new ItemStack(Material.POTION);
        PotionMeta healthMeta = (PotionMeta) healthItem.getItemMeta();

        healthMeta.setColor(Color.fromRGB(230, 10, 10));

        healthMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.health.name").replaceAll("ẞhp", String.valueOf(PlayerCache.tempHp.get(player))));

        ItemUtil.assignPDC("kits_add_gui_next_health_name", healthMeta);

        healthMeta.setLore(NewConfig.getStringList("kits.add_gui.next.health.lore"));

        healthItem.setItemMeta(healthMeta);

        menu.setItem(10, healthItem);

        ItemStack gamemodeItem = new ItemStack(Material.JIGSAW);
        ItemMeta gamemodeMeta = gamemodeItem.getItemMeta();

        gamemodeMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.gamemode.name"));

        ItemUtil.assignPDC("kits_add_gui_next_gamemode_name", gamemodeMeta);

        gamemodeMeta.setLore(List.of(
                NewConfig.getStringList("kits.add_gui.next.gamemode.lore").get(0).replaceAll("ẞa", PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL ? "§a§u" : "§7"),
                NewConfig.getStringList("kits.add_gui.next.gamemode.lore").get(1).replaceAll("ẞb", PlayerCache.tempGamemode.get(player) == Gamemodes.ADVENTURE ? "§a§u" : "§7")
        ));

        gamemodeItem.setItemMeta(gamemodeMeta);

        menu.setItem(13, gamemodeItem);

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);

        ItemMeta mapMeta = mapItem.getItemMeta();

        mapMeta.setDisplayName(NewConfig.getString("kits.add_gui.next.map.name"));

        ItemUtil.assignPDC("kits_add_gui_next_map_name", mapMeta);

        mapMeta.setLore(NewConfig.getStringList("kits.add_gui.next.map.lore"));

        mapItem.setItemMeta(mapMeta);

        menu.setItem(16, mapItem);

        if (
                PlayerCache.tempHp.get(player) != null &&
                PlayerCache.tempGamemode.get(player) != null &&
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

    }

}
