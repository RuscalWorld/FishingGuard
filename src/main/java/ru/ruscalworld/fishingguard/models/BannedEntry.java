package ru.ruscalworld.fishingguard.models;

import net.dv8tion.jda.api.entities.Guild;
import ru.ruscalworld.fishingguard.FishingGuard;
import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.annotations.Property;

import java.sql.Timestamp;

public class BannedEntry extends DefaultModel {
    @Property(column = "guild_id")
    private String guildID;

    @Property(column = "created_at")
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    public Guild getGuild() {
        return FishingGuard.getInstance().jda().getGuildById(this.getGuildID());
    }

    public String getGuildID() {
        return guildID;
    }

    public void setGuildID(String guildID) {
        this.guildID = guildID;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
