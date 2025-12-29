package mel.Polokalap.duelity.Utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public static ArrayList<Player> inDuel = new ArrayList<>();

    public static HashMap<Player, Location> arenaBlue = new HashMap<>();
    public static HashMap<Player, Location> arenaRed = new HashMap<>();

    public static ArrayList<Player> settingArenaIcon= new ArrayList<>();
    public static HashMap<Player, String> arenaName = new HashMap<>();

    public static HashMap<Player, String> editArenaName = new HashMap<>();
    public static ArrayList<String> arenaNames = new ArrayList<>();

    public static HashMap<Player, ArrayList<String>> selectedArenas = new HashMap<>();
    public static HashMap<Player, String> editingKit = new HashMap<>();
    public static ArrayList<Player> inKitEditor = new ArrayList<>();

    public static ArrayList<Player> inPlayerKitEditor = new ArrayList<>();
    public static HashMap<Player, ItemStack[]> playerInventory = new HashMap<>();

    public static HashMap<Player, Map<Player, String>> duelRequests = new HashMap<>();
    public static HashMap<Player, Player> duelOpponent = new HashMap<>();
    public static HashMap<Player, Teams> duelTeams = new HashMap<>();

    public static HashMap<Player, Location> duelPreLocation = new HashMap<>();
    public static HashMap<Player, GameMode> duelPreGameMode = new HashMap<>();
    public static HashMap<Player, Integer> duelRounds = new HashMap<>();
    public static HashMap<Player, Boolean> duelSpectators = new HashMap<>();
    public static HashMap<Player, Integer> duelBlueScores = new HashMap<>();
    public static HashMap<Player, Integer> duelRedScores = new HashMap<>();
    public static HashMap<Player, String> duelKit = new HashMap<>();
    public static HashMap<Player, Boolean> duelEnd = new HashMap<>();

    public static ArrayList<Player> spectating = new ArrayList<>();
    public static HashMap<Player, Player> spectatingPlayer = new HashMap<>();
    public static HashMap<Player, Location> spectatePreLocation = new HashMap<>();
    public static HashMap<Player, GameMode> spectatePreGameMode = new HashMap<>();

    public static int offset = 0;

}