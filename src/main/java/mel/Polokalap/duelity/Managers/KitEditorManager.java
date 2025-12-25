package mel.Polokalap.duelity.Managers;

import mel.Polokalap.duelity.Main;
import mel.Polokalap.duelity.Utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.ArrayList;

public class KitEditorManager {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static void join(Player player) {

        PlayerCache.inPlayerKitEditor.add(player);
        PlayerCache.playerInventory.put(player, player.getInventory().getContents());

        player.setInvulnerable(true);

        player.getInventory().clear();

        ItemStack empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta emptyMeta = empty.getItemMeta();

        emptyMeta.setDisplayName(" ");
        emptyMeta.setHideTooltip(true);

        ItemUtil.assignPDC("unmoveable", emptyMeta);

        empty.setItemMeta(emptyMeta);

        for (int i = 0; i < 36; i++) {

            player.getInventory().setItem(i, empty);

        }

    }

    public static void leave(Player player, Inventory inv, boolean save) {

        player.setInvulnerable(false);

        PlayerCache.inPlayerKitEditor.remove(player);

        player.closeInventory();

        player.getInventory().clear();

        player.getInventory().setContents(PlayerCache.playerInventory.get(player));

        PlayerCache.playerInventory.remove(player);

        if (save) {

            ItemStack helmet = inv.getItem(0);
            ItemStack chestplate = inv.getItem(1);
            ItemStack leggings = inv.getItem(2);
            ItemStack boots = inv.getItem(3);
            ItemStack offhand = inv.getItem(5);

            ArrayList<ItemStack> hotbar = new ArrayList<>();
            ArrayList<ItemStack> rest = new ArrayList<>();

            for (int i = 18; i <= 44; i++) {

                if (inv.getItem(i) == null) {

                    rest.add(new ItemStack(Material.AIR));

                } else {

                    rest.add(inv.getItem(i));

                }

            }
            for (int i = 45; i <= 53; i++) {

                if (inv.getItem(i) == null) {

                    hotbar.add(new ItemStack(Material.AIR));

                } else {

                    hotbar.add(inv.getItem(i));

                }

            }

            ArrayList<ItemStack> master = new ArrayList<>();

            master.addAll(hotbar);
            master.addAll(rest);
            master.add(boots);
            master.add(leggings);
            master.add(chestplate);
            master.add(helmet);
            master.add(offhand);

            ArrayList<NamespacedKey> itemIDs = new ArrayList<>();
            ArrayList<ItemStack> finalList = new ArrayList<>();

            boolean invalid = false;

            for (ItemStack item : master) {

                if (item == null || item.getType() == Material.AIR) {

                    finalList.add(item);
                    continue;

                }

                NamespacedKey key = ItemUtil.getPDCKey(item);

                if (key == null) {

                    finalList.add(item);
                    continue;

                }

                if (itemIDs.contains(key)) {

                    invalid = true;
                    continue;

                }

                itemIDs.add(key);

                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                pdc.remove(key);
                item.setItemMeta(meta);

                finalList.add(item);

            }

            if (invalid) {

                Sound.Error(player);

                for (Player admins : Bukkit.getOnlinePlayers()) {

                    if (admins.hasPermission("duelity.admin")) {

                        admins.sendMessage(NewConfig.getString("admin.kit_editor_alert").replaceAll("%player%", player.getName()));
                        Sound.Ping(admins);

                    }

                }

                Title warning = Title.title(
                        Component.empty(),
                        Component.text(NewConfig.getString("editor.warning")),
                        Title.Times.times(
                                Duration.ofMillis(300),
                                Duration.ofSeconds(1),
                                Duration.ofMillis(300)
                        )
                );

                player.showTitle(warning);

                ConfigurationSection playerKit = KitUtil.getPlayerItems(PlayerCache.editingKit.get(player), player);

                playerKit.set("items", KitUtil.getItems(PlayerCache.editingKit.get(player)).getList("items"));

                plugin.savePlayerConfig();

                return;

            }

            ConfigurationSection kit = KitUtil.getPlayerItems(PlayerCache.editingKit.get(player), player);

            kit.set("items", finalList);

            plugin.savePlayerConfig();

            Title title = Title.title(
                    Component.empty(),
                    Component.text(NewConfig.getString("editor.saved")),
                    Title.Times.times(
                            Duration.ofMillis(300),
                            Duration.ofSeconds(1),
                            Duration.ofMillis(300)
                    )
            );

            player.showTitle(title);

        }

    }

}
