package me.dlabaja.boompvp.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class Setup {

    public void Setup() {
        CreateConfig();
        InitConfig();
        ReadJson();
        if (!DbExists()) {
            Utils.log.info("First time using? Setting things up...");
            CreateConfigDir();
            CreateDB();
            CreateJson();
        }
        Utils.log.info("Setup completed");
    }

    void CreateDB() {
        if (DbExists())
            return;
        Sql.Execute("CREATE TABLE players (" +
                "name varchar(255)," +
                "kills int," +
                "deaths int," +
                "killstreak int" +
                ")");
        Utils.log.info("Database created!");
    }

    void CreateConfig() {
        try {

            var properties = new Properties();
            properties.load(new FileInputStream(Utils.pathToConfig));
            var currproperties = new Properties();
            currproperties.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("config.properties")).openStream());
            if (properties.get("version") != currproperties.get("version")) {
                try {
                    FileUtils.copyURLToFile(Objects.requireNonNull(this.getClass().getClassLoader().getResource("config.properties")), new File("plugins/boompvp/config.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ignored) {
            try {
                FileUtils.copyURLToFile(Objects.requireNonNull(this.getClass().getClassLoader().getResource("config.properties")), new File("plugins/boompvp/config.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void InitConfig() {
        try {
            Config.properties.load(new FileInputStream(Utils.pathToConfig));
            Config.Setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void CreateJson(){
        try {
            FileUtils.copyURLToFile(Objects.requireNonNull(this.getClass().getClassLoader().getResource("spawn.json")), new File("plugins/boompvp/spawn.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ReadJson() {
        try {
            JsonObject maps = JsonParser.parseString(Files.readString(Path.of(Utils.pathToJson), StandardCharsets.US_ASCII)).getAsJsonObject();
            for (var entry: maps.entrySet()) { //the whole json ("maps")
                for (var single_map: JsonParser.parseString(String.valueOf(entry.getValue())).getAsJsonArray()) { //all map objects
                    var obj = single_map.getAsJsonObject();
                    var spwn = single_map.getAsJsonObject().get("spawnpoint").getAsJsonObject();
                    BoomPVPPrvky.maps.put(String.valueOf(obj.get("name")), new Location(Bukkit.getWorld(String.valueOf(spwn.get("world_name").getAsString())), spwn.get("x").getAsInt(), spwn.get("y").getAsInt(), spwn.get("z").getAsInt()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void CreateConfigDir() {
        new File("plugins/boompvp").mkdirs();
    }

    boolean DbExists() {
        return new File(Utils.pathToDB).exists();
    }
}
