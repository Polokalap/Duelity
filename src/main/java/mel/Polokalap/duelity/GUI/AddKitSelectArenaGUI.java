package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class AddKitSelectArenaGUI extends GUI {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("kits.add_gui.next.map.name");
        size = 54;

        super.openGUI(player);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();

        backMeta.setDisplayName(NewConfig.getString("arenas.edit.back.name"));

        ItemUtil.assignPDC("select_arena_add_kit_back_button", backMeta);

        back.setItemMeta(backMeta);

        menu.setItem(4, back);

        ConfigurationSection arenas = plugin.getArenaConfig().getConfigurationSection("arenas");

        if (arenas == null || arenas.getKeys(false).isEmpty()) {

            ItemStack noArenas = new ItemStack(Material.BARRIER);
            ItemMeta noArenasMeta = noArenas.getItemMeta();

            noArenasMeta.setDisplayName(NewConfig.getString("arenas.no_arenas.name"));

            ItemUtil.assignPDC("arena_no_arenas_name", noArenasMeta);

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

            arenaMeta.setLore(null);

            if (!PlayerCache.selectedArenas.containsKey(player)) PlayerCache.selectedArenas.put(player, new ArrayList<>());

            boolean active = PlayerCache.selectedArenas.get(player).contains(name);

            arenaMeta.setEnchantmentGlintOverride(active);

            arenaMeta.setDisplayName(NewConfig.getString("arenas.arena.color") + name);

            ItemUtil.assignPDC("arena-" + name, arenaMeta);

            arenaIcon.setItemMeta(arenaMeta);

            menu.setItem(slot++, arenaIcon);

        }

    }

}
