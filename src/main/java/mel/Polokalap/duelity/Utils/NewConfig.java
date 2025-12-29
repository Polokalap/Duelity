package mel.Polokalap.duelity.Utils;

import mel.Polokalap.duelity.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
        int i = 1;

        for (String line : config.getStringList(string)) {

            if (config.getStringList(string).size() > i) compiled = compiled + line.replaceAll("&", "§") + "\n";
            else compiled = compiled + line.replaceAll("&", "§");

            i++;

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

    public static Component getComponentList(String string, TagResolver... resolvers) {

        MiniMessage mm = MiniMessage.miniMessage();
        Component compiled = Component.empty();
        int i = 1;

        for (String line : config.getStringList(string)) {

            if (config.getStringList(string).size() > i) compiled = compiled.append(mm.deserialize(line, resolvers)).append(Component.newline());
            else compiled = compiled.append(mm.deserialize(line, resolvers));

            i++;

        }

        return compiled;

    }

}
