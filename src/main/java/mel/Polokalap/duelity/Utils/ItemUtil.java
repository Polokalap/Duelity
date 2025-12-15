package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

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

}
