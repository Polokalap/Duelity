package mel.Polokalap.duelity.Listeners;

import mel.Polokalap.duelity.GUI.*;
import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Managers.KitEditorManager;
import mel.Polokalap.duelity.Utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
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
