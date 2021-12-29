package me.dlabaja.boompvp.utils;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Objects;

//třida na práci s databází MongoDB
public class MongoBoomPVP {
    private ObjectId _id;
    @BsonProperty(value = "name")
    private String name;
    @BsonProperty(value = "kills")
    private int kills;
    @BsonProperty(value = "deaths")
    private int deaths;
    @BsonProperty(value = "killstreak")
    private int killstreak;

    public int getKills() {
        return kills;
    }

    public MongoBoomPVP setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public int getDeaths() {
        return deaths;
    }

    public MongoBoomPVP setDeaths(int deaths) {
        this.deaths = deaths;
        return this;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public MongoBoomPVP setKillstreak(int killstreak) {
        this.killstreak = killstreak;
        return this;
    }

    public MongoBoomPVP setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MongoBoomPVP{");
        sb.append("name=").append(name);
        sb.append(", kills=").append(kills);
        sb.append(", deaths=").append(deaths);
        sb.append(", killstreak=").append(killstreak);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MongoBoomPVP MongoBoomPVP = (MongoBoomPVP) o;
        return Objects.equals(name, MongoBoomPVP.name) && Objects.equals(kills,
                MongoBoomPVP.kills) && Objects
                .equals(deaths, MongoBoomPVP.deaths) && Objects.equals(killstreak, MongoBoomPVP.killstreak);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kills, deaths, killstreak);
    }
}



