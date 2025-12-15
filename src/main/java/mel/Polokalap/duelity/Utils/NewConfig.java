package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class NewConfig {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static String getString(String string) {

        return config.getString(string)
                .replaceAll("&", "§")
                .replaceAll("ßprefix", config.getString("PREFIX")
                    .replaceAll("&", "§"));

    }

    public static String getStringCompiled(String string) {

        String compiled = "";

        for (String line : config.getStringList(string)) {

            compiled = compiled + line.replaceAll("&", "§") + "\n";

        }

        return compiled.replaceAll("ßprefix", getString("PREFIX"));

    }

}
