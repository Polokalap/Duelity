package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Random;

public class WorldUtil extends ChunkGenerator {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public void makeEmptyWorld(String name, Boolean block) {

        WorldCreator creator = new WorldCreator(name);
        creator.type(WorldType.FLAT);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(this);

        World world = creator.createWorld();

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);

        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setTime(6000);

        world.setViewDistance(4);
        world.setDifficulty(Difficulty.valueOf(config.getString("settings.difficulty", "HARD")));

        Location spawn = new Location(world, 0, 100, 0);

        world.getChunkAt(spawn).load(true);
        if (block) world.getBlockAt(spawn.clone().add(0, -1, 0)).setType(Material.PACKED_MUD);
        world.setSpawnLocation(spawn);

        // Bukkit.getScheduler().runTaskLater(plugin, () -> WorldEdit.placeSchem(spawn, "test"), 1L);

        PlayerCache.worlds.add(world);

    }

    public static void deleteWorldByWorld(World world) {

        if (world != null) {
            for (Player p : world.getPlayers()) {
                p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
            Bukkit.unloadWorld(world, false);
        }

        File folder = new File(Bukkit.getWorldContainer(), world.getName());
        if (!folder.exists()) return;

        try {

            Files.walk(folder.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    @Override
    public ChunkData generateChunkData(
            World world,
            Random random,
            int chunkX,
            int chunkZ,
            BiomeGrid biome
    ) {
        return createChunkData(world);
    }

}
