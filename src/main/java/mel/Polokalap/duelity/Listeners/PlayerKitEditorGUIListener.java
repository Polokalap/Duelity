package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.GUI.*;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.KitEditorManager;
import mel.Polokalap.duelity.Utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.HashMap;

public class PlayerKitEditorGUIListener implements Listener {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();
    private static FileConfiguration kits = plugin.getKitConfig();

    private HashMap<Player, Long> now = new HashMap<>();
    private int delay = 250;

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (inv.getHolder() instanceof PlayerGUI) {

            Sound.Close(player);

        }

        if (PlayerCache.inPlayerKitEditor.contains(player)) {

            KitEditorManager.leave(player, player.getOpenInventory().getTopInventory(), true);

        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack item = event.getCurrentItem();

        if (item == null) return;

        if (inv.getHolder() instanceof PlayerGUI) {

            if (event.getClickedInventory().equals(event.getView().getBottomInventory())) {

                event.setCancelled(true);
                return;

            }

            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

                event.setCancelled(true);
                return;

            }

            if (
                    event.getAction() == InventoryAction.DROP_ALL_CURSOR ||
                    event.getAction() == InventoryAction.DROP_ALL_SLOT ||
                    event.getAction() == InventoryAction.DROP_ONE_SLOT ||
                    event.getAction() == InventoryAction.DROP_ONE_CURSOR
            ) {

                event.setCancelled(true);

                return;

            }

            if (event.getAction() == InventoryAction.PLACE_ALL && event.getClickedInventory().equals(event.getView().getBottomInventory())) {

                event.setCancelled(true);
                return;

            }

            if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {

                event.setCancelled(true);
                return;

            }

            if (ItemUtil.PDCHelper("unmoveable", item)) {

                event.setCancelled(true);

            }

            ItemStack cursorItem = event.getCursor();

            if (ItemUtil.PDCHelper("temp_helmet", item)) {

                event.setCancelled(true);

                if (
                        cursorItem.getType().equals(Material.LEATHER_HELMET) ||
                        cursorItem.getType().equals(Material.CHAINMAIL_HELMET) ||
                        cursorItem.getType().equals(Material.IRON_HELMET) ||
                        cursorItem.getType().equals(Material.DIAMOND_HELMET) ||
                        cursorItem.getType().equals(Material.NETHERITE_HELMET) ||
                        cursorItem.getType().equals(Material.TURTLE_HELMET) ||
                        cursorItem.getType().equals(Material.CARVED_PUMPKIN) ||
                        cursorItem.getType().equals(Material.PLAYER_HEAD) ||
                        cursorItem.getType().equals(Material.SKELETON_SKULL) ||
                        cursorItem.getType().equals(Material.WITHER_SKELETON_SKULL) ||
                        cursorItem.getType().equals(Material.PIGLIN_HEAD) ||
                        cursorItem.getType().equals(Material.CREEPER_HEAD) ||
                        cursorItem.getType().equals(Material.DRAGON_HEAD)
                ) {

                    event.setCurrentItem(cursorItem);
                    event.setCursor(null);
                    Sound.Swoosh(player);

                }

            }

            if (ItemUtil.PDCHelper("temp_chestplate", item)) {

                event.setCancelled(true);

                if (
                        cursorItem.getType().equals(Material.LEATHER_CHESTPLATE) ||
                        cursorItem.getType().equals(Material.CHAINMAIL_CHESTPLATE) ||
                        cursorItem.getType().equals(Material.IRON_CHESTPLATE) ||
                        cursorItem.getType().equals(Material.DIAMOND_CHESTPLATE) ||
                        cursorItem.getType().equals(Material.NETHERITE_CHESTPLATE) ||
                        cursorItem.getType().equals(Material.ELYTRA)
                ) {

                    event.setCurrentItem(cursorItem);
                    event.setCursor(null);
                    Sound.Swoosh(player);

                }

            }

            if (ItemUtil.PDCHelper("temp_leggings", item)) {

                event.setCancelled(true);

                if (
                        cursorItem.getType().equals(Material.LEATHER_LEGGINGS) ||
                        cursorItem.getType().equals(Material.CHAINMAIL_LEGGINGS) ||
                        cursorItem.getType().equals(Material.IRON_LEGGINGS) ||
                        cursorItem.getType().equals(Material.DIAMOND_LEGGINGS) ||
                        cursorItem.getType().equals(Material.NETHERITE_LEGGINGS)
                ) {

                    event.setCurrentItem(cursorItem);
                    event.setCursor(null);
                    Sound.Swoosh(player);

                }

            }

            if (ItemUtil.PDCHelper("temp_boots", item)) {

                event.setCancelled(true);

                if (
                        cursorItem.getType().equals(Material.LEATHER_BOOTS) ||
                        cursorItem.getType().equals(Material.CHAINMAIL_BOOTS) ||
                        cursorItem.getType().equals(Material.IRON_BOOTS) ||
                        cursorItem.getType().equals(Material.DIAMOND_BOOTS) ||
                        cursorItem.getType().equals(Material.NETHERITE_BOOTS)
                ) {

                    event.setCurrentItem(cursorItem);
                    event.setCursor(null);
                    Sound.Swoosh(player);

                }

            }

            int slot = event.getSlot();

            if (slot == 0 && cursorItem.getType().equals(Material.AIR) && !ItemUtil.PDCHelper("temp_helmet", item)) {

                ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);

                ItemMeta helmetMeta = helmet.getItemMeta();
                helmetMeta.setHideTooltip(true);
                ItemUtil.assignPDC("temp_helmet", helmetMeta);

                helmet.setItemMeta(helmetMeta);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    inv.setItem(0, helmet);
                }, 1L);

                Sound.Swoosh(player);

            }

            if (slot == 1 && cursorItem.getType().equals(Material.AIR) && !ItemUtil.PDCHelper("temp_chestplate", item)) {

                ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);

                ItemMeta chestplateMeta = chestplate.getItemMeta();
                chestplateMeta.setHideTooltip(true);
                ItemUtil.assignPDC("temp_chestplate", chestplateMeta);

                chestplate.setItemMeta(chestplateMeta);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    inv.setItem(1, chestplate);
                }, 1L);

                Sound.Swoosh(player);

            }

            if (slot == 2 && cursorItem.getType().equals(Material.AIR) && !ItemUtil.PDCHelper("temp_leggings", item)) {

                ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);

                ItemMeta leggingsMeta = leggings.getItemMeta();
                leggingsMeta.setHideTooltip(true);
                ItemUtil.assignPDC("temp_leggings", leggingsMeta);

                leggings.setItemMeta(leggingsMeta);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    inv.setItem(2, leggings);
                }, 1L);

                Sound.Swoosh(player);

            }

            if (slot == 3 && cursorItem.getType().equals(Material.AIR) && !ItemUtil.PDCHelper("temp_boots", item)) {

                ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);

                ItemMeta bootsMeta = boots.getItemMeta();
                bootsMeta.setHideTooltip(true);
                ItemUtil.assignPDC("temp_boots", bootsMeta);

                boots.setItemMeta(bootsMeta);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    inv.setItem(3, boots);
                }, 1L);

                Sound.Swoosh(player);

            }

            String name = item.getItemMeta().getDisplayName();

            if (now.get(player) == null) now.put(player, System.currentTimeMillis());

            if (System.currentTimeMillis() - now.get(player) < delay) return;

            now.put(player, System.currentTimeMillis());

            if (ItemUtil.PDCHelper("player_kit_editor_save", item)) {

                Sound.Click(player);
                KitEditorManager.leave(player, player.getOpenInventory().getTopInventory(), true);
                event.setCancelled(true);

            }

            if (ItemUtil.PDCHelper("player_kit_editor_reset", item)) {

                Sound.Click(player);

                ConfigurationSection playerKit = KitUtil.getPlayerItems(PlayerCache.editingKit.get(player), player);

                playerKit.set("items", KitUtil.getItems(PlayerCache.editingKit.get(player)).getList("items"));

                plugin.savePlayerConfig();

                event.setCancelled(true);

                Title reset = Title.title(
                        Component.empty(),
                        Component.text(NewConfig.getString("editor.reset")),
                        Title.Times.times(
                                Duration.ofMillis(300),
                                Duration.ofSeconds(1),
                                Duration.ofMillis(300)
                        )
                );

                player.showTitle(reset);

                KitEditorManager.leave(player, player.getOpenInventory().getTopInventory(), false);

            }

        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.inPlayerKitEditor.contains(player)) {

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.inPlayerKitEditor.contains(player)) {

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {

        Player player = (Player) event.getPlayer();

        if (PlayerCache.inPlayerKitEditor.contains(player)) {

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onSwapItems(PlayerSwapHandItemsEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.inPlayerKitEditor.contains(player)) {

            event.setCancelled(true);

        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (PlayerCache.inPlayerKitEditor.contains(player)) {

            KitEditorManager.leave(player, player.getOpenInventory().getTopInventory(), true);

        }

    }

}
