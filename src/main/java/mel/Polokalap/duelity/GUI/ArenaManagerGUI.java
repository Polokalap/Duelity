package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class ArenaManagerGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("arenas.name");
        size = 54;

        super.openGUI(player);

        ItemStack addArena = new ItemStack(Material.OAK_STAIRS);
        ItemMeta addMeta = addArena.getItemMeta();

        addMeta.setDisplayName(NewConfig.getString("arenas.add.name"));

        addMeta.setLore(NewConfig.getStringList("arenas.add.lore"));

        addArena.setItemMeta(addMeta);

        menu.setItem(4, addArena);

        ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

        if (arenas == null || arenas.getKeys(false).isEmpty()) {

            ItemStack noArenas = new ItemStack(Material.BARRIER);
            ItemMeta noArenasMeta = noArenas.getItemMeta();

            noArenasMeta.setDisplayName(NewConfig.getString("arenas.no_arenas.name"));

            noArenasMeta.setLore(NewConfig.getStringList("arenas.no_arenas.lore"));

            noArenas.setItemMeta(noArenasMeta);

            menu.setItem(10, noArenas);

            return;

        }

        HashMap<String, Material> arenasList = new HashMap<>();

        int slot = 10;

        for (String arenaId : arenas.getKeys(false)) {

            ConfigurationSection arena = arenas.getConfigurationSection(arenaId);

            if (
                    slot == 17 ||
                    slot == 26 ||
                    slot == 35 ||
                    slot == 44
            ) slot += 2;

            String name = arena.getString("name");
            Material icon = Material.valueOf(arena.getString("icon"));

            arenasList.put(name, icon);

            ItemStack arenaIcon = new ItemStack(icon);
            ItemMeta arenaMeta = arenaIcon.getItemMeta();

            arenaMeta.setDisplayName(NewConfig.getString("arenas.arena.color") + name);

            arenaMeta.setLore(NewConfig.getStringList("arenas.arena.lore"));

            arenaIcon.setItemMeta(arenaMeta);

            menu.setItem(slot++, arenaIcon);

        }

    }

}
