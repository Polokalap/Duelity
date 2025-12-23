package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitUtil {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();
    private static FileConfiguration kits = plugin.getKitConfig();

    public static ConfigurationSection getItems(String name) {

        boolean exists = false;
        ConfigurationSection finalKit = null;
        ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

        if (kitsSection == null) {
            kitsSection = kits.createSection("kits");
        }

        for (String key : kitsSection.getKeys(false)) {

            ConfigurationSection kit = kitsSection.getConfigurationSection(key);
            if (kit == null) continue;

            String kitName = kit.getString("name");
            if (kitName == null) continue;

            if (kitName.equals(name)) {

                exists = true;
                finalKit = kit;

            }

        }

        if (!exists) {

            plugin.getLogger().info("Kit " + name + " doesn't exist!");
            return null;

        }

        return finalKit;

    }

    public static void claimKit(String name, Player player) {

        ConfigurationSection finalKit = getItems(name);

        player.getInventory().clear();

        List<?> items = finalKit.getList("items");
        int slot = 0;
        int armorStart = items.size() - 5;

        for (int i = 0; i < armorStart; i++) {

            Object obj = items.get(i);

            if (!(obj instanceof ItemStack item)) continue;

            player.getInventory().setItem(slot++, item);

        }

        player.getInventory().setBoots((ItemStack) items.get(armorStart));
        player.getInventory().setLeggings((ItemStack) items.get(armorStart + 1));
        player.getInventory().setChestplate((ItemStack) items.get(armorStart + 2));
        player.getInventory().setHelmet((ItemStack) items.get(armorStart + 3));

        player.getInventory().setItemInOffHand((ItemStack) items.get(armorStart + 4));

    }

}
