package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.GUI.*;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;

public class EditKitGUIListener implements Listener {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    private HashMap<Player, Long> now = new HashMap<>();
    private int delay = 250;

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();

        if (inv.getHolder() instanceof EditorGUI) {

            if (!event.getView().getTitle().equals(NewConfig.getString("editor.name"))) return;

            if (event.getView().getTopInventory() == event.getClickedInventory() && event.getSlot() >= 5 && event.getSlot() <= 17) event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) return;

            String name = item.getItemMeta().getDisplayName();

            if (now.get(player) == null) now.put(player, System.currentTimeMillis());

            if (System.currentTimeMillis() - now.get(player) < delay) return;

            now.put(player, System.currentTimeMillis());

            if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

            switch (event.getAction()) {

                case PICKUP_ALL:
                case PICKUP_HALF:
                case PICKUP_SOME:
                case PICKUP_ONE:
                case PLACE_ALL:
                case PLACE_ONE:
                case PLACE_SOME:

                    if (!(event.getView().getTopInventory() == event.getClickedInventory() && event.getSlot() >= 5 && event.getSlot() <= 17)) Sound.Click(player);

                    break;

            }

            if (ItemUtil.PDCHelper("editor_admin_import", item)) {

                Inventory top = event.getView().getTopInventory();

                ItemStack helmet;
                ItemStack chestplate;
                ItemStack leggings;
                ItemStack boots;

                if (player.getInventory().getHelmet() != null) helmet = player.getInventory().getHelmet().clone();
                else helmet = new ItemStack(Material.AIR);

                if (player.getInventory().getChestplate() != null) chestplate = player.getInventory().getChestplate().clone();
                else chestplate = new ItemStack(Material.AIR);

                if (player.getInventory().getLeggings() != null) leggings = player.getInventory().getLeggings().clone();
                else leggings = new ItemStack(Material.AIR);

                if (player.getInventory().getBoots() != null) boots = player.getInventory().getBoots().clone();
                else boots = new ItemStack(Material.AIR);

                top.setItem(0, helmet);
                top.setItem(1, chestplate);
                top.setItem(2, leggings);
                top.setItem(3, boots);
                top.setItem(4, player.getInventory().getItemInOffHand().clone());

                int slot = 18;
                int slot2 = 45;
                int counter = 0;
                boolean items = true;

                ArrayList<ItemStack> hotbar = new ArrayList<>();

                for (ItemStack playerItem : player.getInventory()) {

                    if (counter <= 8) {

                        hotbar.add(playerItem);
                        counter++;

                    } else {

                        if (items) top.setItem(slot, playerItem);
                        if (slot >= 44) items = false;
                        slot++;

                    }

                }

                for (ItemStack playerItem2 : hotbar) {

                    top.setItem(slot2, playerItem2);
                    slot2++;

                }

                event.setCancelled(true);

            }

            if (ItemUtil.PDCHelper("editor_admin_options", item)) {

                new AdminEditKitGUI().openGUI(player);

            }

            if (ItemUtil.PDCHelper("editor_edit_gamemode_name", item)) {

                if (PlayerCache.tempGamemode.get(player) == Gamemodes.SURVIVAL) PlayerCache.tempGamemode.put(player, Gamemodes.ADVENTURE);
                else PlayerCache.tempGamemode.put(player, Gamemodes.SURVIVAL);

                ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

                kit.set("gamemode", PlayerCache.tempGamemode.get(player));

                plugin.saveKitConfig();

            }

            if (ItemUtil.PDCHelper("admin_kit_item_editor_back_button", item)) {

                ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

                ArrayList<ItemStack> hotbar = new ArrayList<>();
                ArrayList<ItemStack> inventory = new ArrayList<>();
                Inventory top = event.getView().getTopInventory();

                ItemStack helmet = top.getItem(0);
                ItemStack chestplate = top.getItem(1);
                ItemStack leggings = top.getItem(2);
                ItemStack boots = top.getItem(3);
                ItemStack offhand = top.getItem(4);

                ArrayList<ItemStack> master = new ArrayList<>();

                for (int slot = 18; slot <= 44; slot++) {
                    if (top.getItem(slot) != null) inventory.add(top.getItem(slot));
                    else inventory.add(new ItemStack(Material.AIR));
                }

                for (int slot = 45; slot <= 53; slot++) {
                    if (top.getItem(slot) != null) hotbar.add(top.getItem(slot));
                    else hotbar.add(new ItemStack(Material.AIR));
                }

                master.addAll(hotbar);
                master.addAll(inventory);

                master.add(boots);
                master.add(leggings);
                master.add(chestplate);
                master.add(helmet);
                master.add(offhand);

                kit.set("items", master);
                kit.set("health", PlayerCache.tempHp.get(player));
                kit.set("regen", PlayerCache.tempRegen.get(player));

                plugin.saveKitConfig();

                new KitsManagerGUI().openGUI(player);

            }

        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (inv.getHolder() instanceof EditorGUI) {

            Sound.Close(player);

            if (PlayerCache.inKitEditor.contains(player)) {

                ConfigurationSection kit = KitUtil.getItems(PlayerCache.editingKit.get(player));

                ArrayList<ItemStack> hotbar = new ArrayList<>();
                ArrayList<ItemStack> inventory = new ArrayList<>();
                Inventory top = event.getView().getTopInventory();

                ItemStack helmet = top.getItem(0);
                ItemStack chestplate = top.getItem(1);
                ItemStack leggings = top.getItem(2);
                ItemStack boots = top.getItem(3);
                ItemStack offhand = top.getItem(4);

                ArrayList<ItemStack> master = new ArrayList<>();

                for (int slot = 18; slot <= 44; slot++) {
                    if (top.getItem(slot) != null) inventory.add(top.getItem(slot));
                    else inventory.add(new ItemStack(Material.AIR));
                }

                for (int slot = 45; slot <= 53; slot++) {
                    if (top.getItem(slot) != null) hotbar.add(top.getItem(slot));
                    else hotbar.add(new ItemStack(Material.AIR));
                }

                master.addAll(hotbar);
                master.addAll(inventory);

                master.add(boots);
                master.add(leggings);
                master.add(chestplate);
                master.add(helmet);
                master.add(offhand);

                kit.set("items", master);

                plugin.saveKitConfig();

                PlayerCache.inKitEditor.remove(player);

            }

        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        Player player = event.getPlayer();

        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof EditorGUI) {

            Sound.Swoosh(player);
            event.getItemDrop().remove();

        }

    }

}
