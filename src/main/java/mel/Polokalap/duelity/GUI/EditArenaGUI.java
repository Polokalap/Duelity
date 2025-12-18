package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class EditArenaGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("arenas.edit.name").replaceAll("ẞname", PlayerCache.editArenaName.get(player));
        size = 27;

        super.openGUI(player);

        ItemStack nameArena = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta nameMeta = nameArena.getItemMeta();

        nameMeta.setDisplayName(NewConfig.getString("arenas.edit.edit_name.name"));

        nameMeta.setLore(NewConfig.getStringList("arenas.edit.edit_name.lore"));

        nameArena.setItemMeta(nameMeta);

        menu.setItem(10, nameArena);

        ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

        Material material = Material.STICK;

        for (String arenaId : arenas.getKeys(false)) {

            ConfigurationSection arena = arenas.getConfigurationSection(arenaId);

            if (arena.get("name").equals(PlayerCache.editArenaName.get(player))) material = Material.valueOf(arena.get("icon").toString());

        }

        ItemStack iconArena = new ItemStack(material);
        ItemMeta iconMeta = iconArena.getItemMeta();

        iconMeta.setDisplayName(NewConfig.getString("arenas.edit.icon.name"));

        iconMeta.setLore(NewConfig.getStringList("arenas.edit.icon.lore"));

        iconArena.setItemMeta(iconMeta);

        menu.setItem(13, iconArena);

        ItemStack deleteArena = new ItemStack(Material.BARRIER);
        ItemMeta deleteMeta = deleteArena.getItemMeta();

        deleteMeta.setDisplayName(NewConfig.getString("arenas.edit.delete.name"));

        deleteMeta.setLore(NewConfig.getStringList("arenas.edit.delete.lore"));

        deleteArena.setItemMeta(deleteMeta);

        menu.setItem(16, deleteArena);

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();

        backMeta.setDisplayName(NewConfig.getString("arenas.edit.back.name"));

        backButton.setItemMeta(backMeta);

        menu.setItem(26, backButton);

    }

}
