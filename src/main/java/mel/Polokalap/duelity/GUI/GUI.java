package mel.Polokalap.duelity.GUI;

import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI implements InventoryHolder {

    Main plugin = Main.getInstance();
    FileConfiguration config = plugin.getConfig();
    FileConfiguration kits = plugin.getKitConfig();

    int size;
    String name;
    Inventory menu;

    public void openGUI(Player player) {

        menu = Bukkit.createInventory(this, size, name);

        ItemStack empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta emptyMeta = empty.getItemMeta();

        emptyMeta.setDisplayName(" ");

        empty.setItemMeta(emptyMeta);

        for (int i = 0; i < menu.getSize(); i++) {

            menu.setItem(i, empty);

        }

        player.openInventory(menu);

        Sound.Open(player);

    }

    @Override
    public Inventory getInventory() {

        return null;

    }

}
