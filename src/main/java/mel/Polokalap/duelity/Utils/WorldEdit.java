package mel.Polokalap.duelity.Utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.io.Closer;
import mel.Polokalap.duelity.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorldEdit {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static void saveSchem(Location loc1, Location loc2, String path) {

        File schemFile = new File(plugin.getDataFolder(), "Maps/" + path + ".schem");

        if (!schemFile.getParentFile().exists()) schemFile.getParentFile().mkdirs();

        Region reg = new CuboidRegion(BukkitAdapter.asBlockVector(loc1), BukkitAdapter.asBlockVector(loc2));
        EditSession editSession = makeEditSession(loc1.getWorld());

        BlockArrayClipboard clipboard = new BlockArrayClipboard(reg);
        ForwardExtentCopy copy = new ForwardExtentCopy(editSession, reg, clipboard, reg.getMinimumPoint());

        try {

            Operations.complete(copy);

        } catch (final Throwable t) {

            throw new RuntimeException(t);

        }

        try (Closer closer = Closer.create()) {

            FileOutputStream outputStream = closer.register(new FileOutputStream(schemFile));
            ClipboardWriter writer = closer.register(BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(outputStream));

            writer.write(clipboard);

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }

    public static void placeSchem(Location loc, String path) {

        File schemFile = new File(plugin.getDataFolder(), "Maps/" + path + ".schem");

        if (!schemFile.getParentFile().exists()) schemFile.getParentFile().mkdirs();

        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);

        try (FileInputStream fis = new FileInputStream(schemFile);

             ClipboardReader reader = format.getReader(fis);
             EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance()
                     .newEditSession(BukkitAdapter.adapt(loc.getWorld()))) {

            Clipboard clipboard = reader.read();

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BukkitAdapter.asBlockVector(loc))
                    .build();

            Operations.complete(operation);

        } catch (Exception e) {

            throw new RuntimeException("Failed to paste schematic", e);

        }

    }

    private static EditSession makeEditSession(World bukkitWorld) {

        final EditSession session = com.sk89q.worldedit.WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(bukkitWorld));

        session.setSideEffectApplier(SideEffectSet.defaults());

        return session;

    }

}
