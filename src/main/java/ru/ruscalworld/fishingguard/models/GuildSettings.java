package ru.ruscalworld.fishingguard.models;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.fishingguard.FishingGuard;
import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;
import ru.ruscalworld.storagelib.exceptions.NotFoundException;

import java.sql.Timestamp;
import java.util.Optional;

@Model(table = "guilds")
public class GuildSettings extends DefaultModel {
    private static final Logger logger = LoggerFactory.getLogger("GuildSettings");
    private static final Storage storage = FishingGuard.getInstance().storage();

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

    public static GuildSettings getByGuild(Guild guild) {
        try {
            return storage.find(GuildSettings.class, "guild_id", guild.getId());
        } catch (Exception ignored) { }
        return new GuildSettings(guild.getId());
    }

    public Guild getGuild() {
        return FishingGuard.getInstance().jda().getGuildById(this.getGuildID());
    }

    public String getGuildID() {
        return guildID;
    }

    public Optional<Invite> getInvite() {
        if (this.getInviteCode() == null) {
            if (!this.getGuild().getSelfMember().hasPermission(Permission.CREATE_INSTANT_INVITE)) return Optional.empty();
            TextChannel channel = this.getGuild().getRulesChannel();
            if (channel == null) channel = this.getGuild().getDefaultChannel();
            if (channel == null) channel = this.getGuild().getTextChannels().get(0);
            if (channel == null) return Optional.empty();

            try {
                return Optional.ofNullable(channel.createInvite().setMaxAge(0).complete());
            } catch (Exception exception) {
                logger.warn("Unable to create invite for guild {}", this.getGuild().getName(), exception);
                return Optional.empty();
            }
        }

        return Optional.ofNullable(Invite.resolve(FishingGuard.getInstance().jda(), this.getInviteCode()).complete());
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
