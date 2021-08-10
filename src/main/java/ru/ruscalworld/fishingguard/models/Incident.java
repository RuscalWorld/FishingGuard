package ru.ruscalworld.fishingguard.models;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import ru.ruscalworld.fishingguard.FishingGuard;
import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;

import java.sql.Timestamp;

@Model(table = "incidents")
public class Incident extends DefaultModel {
    @Property(column = "member_id")
    private String memberID;

    @Property(column = "guild_id")
    private String guildID;

    @Property(column = "text")
    private String text;

    @Property(column = "created_at")
    private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    public Incident() {

    }

    public Member getMember() {
        return this.getGuild().getMemberById(this.getMemberID());
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public Guild getGuild() {
        return FishingGuard.getInstance().jda().getGuildById(this.getGuildID());
    }

    public String getGuildID() {
        return guildID;
    }

    public void setGuildID(String guildID) {
        this.guildID = guildID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
