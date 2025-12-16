package mel.Polokalap.duelity.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerCache {

    public static HashMap<Player, String> tempName = new HashMap<>();
    public static HashMap<Player, Material> tempIcon = new HashMap<>();
    public static HashMap<Player, ArrayList<ItemStack>> tempKit = new HashMap<>();

    public static HashMap<Player, Integer> tempHp = new HashMap<>();
    public static HashMap<Player, Gamemodes> tempGamemode = new HashMap<>();

    public static HashMap<Player, Location> regP = new HashMap<>();
    public static HashMap<Player, Location> regS = new HashMap<>();

    public static ArrayList<World> worlds = new ArrayList<>();
    public static ArrayList<Player> editingArena = new ArrayList<>();
    public static HashMap<Player, World> playerWorld = new HashMap<>();

    public static int arenaOffset = 0;

}
