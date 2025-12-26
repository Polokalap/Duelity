package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Utils.ItemUtil;
import mel.Polokalap.duelity.Utils.NewConfig;
import mel.Polokalap.duelity.Utils.PlayerCache;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class DuelGUI extends GUI implements InventoryHolder {

    @Override
    public void openGUI(Player player) {

        name = NewConfig.getString("duel.name").replaceAll("%player%", PlayerCache.duelOpponent.get(player).getName());
        holder = new DuelGUI();
        size = 54;

        super.openGUI(player);

        ConfigurationSection playerSettings = plugin.getPlayerConfig().getConfigurationSection("players." + player.getUniqueId() + ".settings");

        ItemStack addKit = new ItemStack(Material.ENDER_EYE);
        ItemMeta addMeta = addKit.getItemMeta();

        addMeta.setDisplayName(NewConfig.getString("duel.gui.rounds.name").replaceAll("%rounds%", String.valueOf(playerSettings.getInt("rounds"))));

        ItemUtil.assignPDC("duel_rounds", addMeta);

        addMeta.setLore(NewConfig.getStringList("duel.gui.rounds.lore"));

        addKit.setItemMeta(addMeta);

        menu.setItem(48, addKit);

        ItemStack spectate = new ItemStack(Material.TARGET);
        ItemMeta spectateMeta = spectate.getItemMeta();

        spectateMeta.setDisplayName(NewConfig.getString("duel.gui.spectate.name").replaceAll("%state%", playerSettings.getBoolean("spectate") ? NewConfig.getString("player.on") : NewConfig.getString("player.off")));

        ItemUtil.assignPDC("duel_spectate_toggle", spectateMeta);

        spectateMeta.setLore(NewConfig.getStringList("duel.gui.spectate.lore"));

        spectate.setItemMeta(spectateMeta);

        menu.setItem(49, spectate);

        ItemStack unmoveable = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta unmoveableMeta = unmoveable.getItemMeta();

        unmoveableMeta.setDisplayName(" ");

        ItemUtil.assignPDC("unmoveable", unmoveableMeta);

        unmoveableMeta.setHideTooltip(true);

        unmoveable.setItemMeta(unmoveableMeta);

        for (int i = 0; i <= 53; i++) if (i > 10 && i < 17 || i > 18 && i < 26 || i > 27 && i < 35 || i > 36 && i < 44) menu.setItem(i, unmoveable);

        ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

        if (kitsSection == null || kitsSection.getKeys(false).isEmpty()) {

            ItemStack noKitsItem = new ItemStack(Material.BARRIER);
            ItemMeta noKitsMeta = noKitsItem.getItemMeta();

            noKitsMeta.setDisplayName(NewConfig.getString("duel.no_kits.name"));

            ItemUtil.assignPDC("duel_no_kits_name", noKitsMeta);

            noKitsMeta.setLore(NewConfig.getStringList("kits.no_kits.lore"));

            noKitsItem.setItemMeta(noKitsMeta);

            menu.setItem(10, noKitsItem);

            return;

        }

        HashMap<String, Material> kitsList = new HashMap<>();

        int slot = 10;

        for (String kitId : kitsSection.getKeys(false)) {

            ConfigurationSection kit = kitsSection.getConfigurationSection(kitId);

            if (
                    slot == 17 ||
                            slot == 26 ||
                            slot == 35 ||
                            slot == 44
            ) slot += 2;

            String name = kit.getString("name");
            Material icon = Material.valueOf(kit.getString("icon"));

            kitsList.put(name, icon);

            ItemStack kitIcon = new ItemStack(icon);
            ItemMeta kitMeta = kitIcon.getItemMeta();

            kitMeta.setDisplayName(NewConfig.getString("duel.color") + name);

            ItemUtil.assignPDC("duel-" + name, kitMeta);

            kitIcon.setItemMeta(kitMeta);

            menu.setItem(slot++, kitIcon);

        }

    }

    @Override
    public Inventory getInventory() {

        return super.getInventory();

    }

}
