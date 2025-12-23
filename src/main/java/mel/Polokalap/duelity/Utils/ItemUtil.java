package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtil {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static void saveItemStack(String path, ItemStack item, FileConfiguration configuration) {

        configuration.set(path, item);

    }

    public static ItemStack getItemStack(String path, FileConfiguration configuration) {

        ItemStack item = configuration.getItemStack(path);

        return item;

    }

    public static boolean PDCHelper(String key, ItemStack item) {

        if (!item.hasItemMeta() || item.getItemMeta() == null) return false;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        NamespacedKey usedKey = new NamespacedKey(plugin, key);

        return pdc.has(usedKey, PersistentDataType.INTEGER);

    }

    public static void assignPDC(String key, ItemMeta meta) {

        NamespacedKey newKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(newKey, PersistentDataType.INTEGER, 42);

    }

}
