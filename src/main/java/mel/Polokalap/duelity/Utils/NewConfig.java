package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class NewConfig {

    private static Main plugin = Main.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static String getString(String string) {

        String message = config.getString(string);
        message = message.replaceAll("&", "§");
        message = message.replaceAll("ẞprefix", config.getString("PREFIX")
                .replaceAll("&", "§"));

        return message;

    }

    public static String getStringCompiled(String string) {

        String compiled = "";

        for (String line : config.getStringList(string)) {

            compiled = compiled + line.replaceAll("&", "§") + "\n";

        }

        return compiled.replaceAll("ßprefix", getString("PREFIX"));

    }

    public static List<String> getStringList(String string) {

        List<String> stringList = new ArrayList<>();

        for (String line : config.getStringList(string)) {

            stringList.add(line
                    .replaceAll("&", "§")
                    .replaceAll("ßprefix", getString("PREFIX")
            ));

        }

        return stringList;

    }

}
