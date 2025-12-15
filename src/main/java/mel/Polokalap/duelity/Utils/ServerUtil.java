package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class ServerUtil {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static Boolean isServerSetupDone() {

        boolean check = true;

        if (!config.getBoolean("settings.setup")) check = false;
        if (config.getString("settings.spawn") == null) check = false;

        return check;

    }

    public static int getSetupErrorCode() {

        int check = 0;

        if (!config.getBoolean("settings.setup")) check = 1;
        if (config.getString("settings.spawn") == null) check = 2;

        return check;

    }

}
