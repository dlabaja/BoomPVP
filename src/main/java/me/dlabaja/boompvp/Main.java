package me.dlabaja.boompvp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dlabaja.boompvp.utils.BoomPVPPrvky;
import me.dlabaja.boompvp.utils.Config;
import me.dlabaja.boompvp.utils.Setup;
import me.dlabaja.boompvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static me.dlabaja.boompvp.utils.Utils.log;

public final class Main extends JavaPlugin {

    int time = 0;
    WorldBorder border;
    BoomPVPPrvky _boomPVPPrvky = new BoomPVPPrvky();

    @Override
    public void onEnable() {
        log = getLogger();
        log.info("BoomPVP ON");
        new Setup().Setup();

        _boomPVPPrvky.listLokace.addAll(BoomPVPPrvky.maps.keySet());
        for (int i = 0; i < BoomPVPPrvky.maps.keySet().size(); i++) {
            _boomPVPPrvky.mapToName.put(_boomPVPPrvky.listLokace.get(i), BoomPVPPrvky.maps.get(_boomPVPPrvky.listLokace.get(i)));
        }


        /*_boomPVPPrvky.listLokace = new ArrayList<>(List.of(
                new Location(Bukkit.getWorld("world"), 11, 52, -0.5),
                new Location(Bukkit.getWorld("world"), -2, 54, 502.5),
                new Location(Bukkit.getWorld("world"), 24.5, 52, -492),
                new Location(Bukkit.getWorld("world"), 497, 18, 492)));
        _boomPVPPrvky.mapToName = new HashMap<>(){{
            put(_boomPVPPrvky.listLokace.get(0), "OREO");
            put(_boomPVPPrvky.listLokace.get(1), "CITY");
            put(_boomPVPPrvky.listLokace.get(2), "BUILDING");
            put(_boomPVPPrvky.listLokace.get(3), "VOLCANO");
        }};*/

        var timer = Config.time;
        Commands cmd = new Commands();
        Objects.requireNonNull(this.getCommand("boomkit")).setExecutor(cmd);
        getServer().getPluginManager().registerEvents(new BoomPVP(), this);

        border = Objects.requireNonNull(Bukkit.getWorld("world")).getWorldBorder();
        border.setDamageAmount(Config.world_border_damage);
        border.setDamageBuffer(0);
        GetFirstMap(_boomPVPPrvky.listLokace);

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (var player : Bukkit.getOnlinePlayers()) {
                    player.setLevel(timer - time);
                    if (timer - time <= 5)
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    if (time == 1)
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }

                if (time == timer - (timer / 5)) {
                    border.setSize(Config.world_border_min_size, timer - time);
                }

                if (time == timer) {
                    SwitchMap(_boomPVPPrvky.listLokace);
                    time = 0;
                }
                time++;
            }
        }, 0, 20);
    }

    @Override
    public void onDisable() {
        Utils.log.info("BoomPVP OFF");
    }

    public void GetFirstMap(List<Location> listLokace) {
        BoomPVPPrvky.currentLocation = GetNewMap(listLokace);
        border.setCenter(BoomPVPPrvky.currentLocation);
        border.setSize(Config.world_border_size);
    }

    public Location GetNewMap(List<Location> listLokace) {
        var lokace = BoomPVPPrvky.currentLocation;
        var rndLokace = listLokace.get(new Random().nextInt(listLokace.size()));
        while (rndLokace == lokace) {
            rndLokace = listLokace.get(new Random().nextInt(listLokace.size()));
        }
        return rndLokace;
    }

    public void SwitchMap(List<Location> listLokace) {
        BoomPVPPrvky.currentLocation = GetNewMap(listLokace);
        System.out.println(BoomPVPPrvky.currentLocation);
        Objects.requireNonNull(Bukkit.getWorld("world")).setFullTime(_boomPVPPrvky.dayTimeList[new Random().nextInt(_boomPVPPrvky.dayTimeList.length)]);
        for (var player : Bukkit.getOnlinePlayers()) {
            player.teleport(BoomPVPPrvky.currentLocation);
            //player.sendTitle(_boomPVPPrvky.mapToName.get(BoomPVPPrvky.currentLocation), "", 5, 40, 5);
        }
        border.setCenter(BoomPVPPrvky.currentLocation);
        border.setSize(Config.world_border_size);
    }
}

