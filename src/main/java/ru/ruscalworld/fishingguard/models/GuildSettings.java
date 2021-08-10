package ru.ruscalworld.fishingguard.models;

import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;

import java.sql.Timestamp;

@Model(table = "guilds")
public class GuildSettings extends DefaultModel {
    @Property(column = "guild_id")
    private String guildID;

    @Property(column = "invite_code")
    private String inviteCode;

    @Property(column = "created_at")
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    public GuildSettings() {

    }

    public GuildSettings(String guildID) {
        this.guildID = guildID;
    }

    public String getGuildID() {
        return guildID;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
