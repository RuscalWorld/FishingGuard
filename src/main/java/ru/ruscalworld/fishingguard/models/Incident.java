package ru.ruscalworld.fishingguard.models;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import ru.ruscalworld.fishingguard.FishingGuard;
import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;
import ru.ruscalworld.storagelib.exceptions.InvalidModelException;
import ru.ruscalworld.storagelib.exceptions.NotFoundException;

import java.sql.SQLException;
import java.sql.Timestamp;

@Model(table = "incidents")
public class Incident extends DefaultModel {
    private static final Storage storage = FishingGuard.getInstance().storage();

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

    public static Incident create(Member member, String text) throws InvalidModelException, SQLException, NotFoundException {
        Incident incident = new Incident();
        incident.setMemberID(member.getId());
        incident.setGuildID(member.getGuild().getId());
        incident.setText(text);

        long id = storage.save(incident);
        return storage.retrieve(Incident.class, id);
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
