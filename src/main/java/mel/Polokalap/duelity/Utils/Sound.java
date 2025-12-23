package mel.Polokalap.duelity.Utils;

import org.bukkit.entity.Player;

public class Sound {

    public static void Open(Player player) {

        player.playSound(player, org.bukkit.Sound.BLOCK_ENDER_CHEST_OPEN, 0.5f, 1.0f);

    }

    public static void Close(Player player) {

        player.playSound(player, org.bukkit.Sound.BLOCK_ENDER_CHEST_CLOSE, 0.5f, 1.0f);

    }

    public static void Ping(Player player) {

        player.playSound(player, org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);

    }

    public static void Click(Player player) {

        player.playSound(player, org.bukkit.Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, 0.5f, 1.0f);

    }

    public static void Error(Player player) {

        player.playSound(player, org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);

    }

    public static void Drink(Player player, Boolean negative) {

        player.playSound(player, org.bukkit.Sound.ENTITY_GENERIC_DRINK, 0.5f, negative ? 0.9f : 1.0f);

    }

    public static void Swoosh(Player player) {

        player.playSound(player, org.bukkit.Sound.ENTITY_ARMADILLO_BRUSH, 0.5f, 1.0f);

    }

}
