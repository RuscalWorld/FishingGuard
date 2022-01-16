package ru.ruscalworld.fishingguard.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.fishingguard.models.GuildSettings;
import ru.ruscalworld.fishingguard.models.Incident;
import ru.ruscalworld.fishingguard.util.LinkManager;

import java.util.Optional;

public class MessageListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;

        try {
            if (LinkManager.checkMessage(event.getMessage())) {
                event.getMessage().delete().queue();
                Incident.create(event.getMember(), event.getMessage().getContentRaw());

                GuildSettings settings = GuildSettings.getByGuild(event.getGuild());
                Optional<Invite> invite = settings.getInvite();
                Member owner = event.getGuild().getOwner();
                String inviteUrl = event.getJDA().getInviteUrl(Permission.KICK_MEMBERS, Permission.MESSAGE_MANAGE, Permission.CREATE_INSTANT_INVITE);

                StringBuilder message = new StringBuilder();
                message.append("Привет. Ты получил это сообщение, поскольку мы заметили подозрительную активность на сервере `")
                        .append(event.getGuild().getName()).append("`. Скорее всего, твой аккаунт был взломан и использован для ")
                        .append("рассылки фишинговых ссылок. Настоятельно рекомендуем изменить пароль и включить двухфакторную ")
                        .append("аутентификацию, чтобы подобная ситуация не повторилась. Порекомендуй своим друзьям сделать ")
                        .append("то же самое, чтобы обезопасить их аккаунты. \n\n");

                if (invite.isPresent()) {
                    message.append("Если ты был исключён с сервера, то можешь вернуться, используя приглашение: ")
                            .append(invite.get().getUrl());
                } else {
                    message.append("Если ты был исключён с сервера, то тебе следует связаться с его администратором, " +
                            "т.к. мы не смогли создать приглашение для тебя. ");
                    if (owner != null) message.append("Владелец сервера `")
                            .append(event.getGuild().getName()).append("`: ")
                            .append("`").append(owner.getUser().getAsTag()).append("`.");
                }

                message.append("\n\n");
                message.append("Сообщение, которое мы сочли подозрительным: ```md\n").append(event.getMessage().getContentRaw()).append("\n``` \n");
                message.append("А если ты сам являешься администратором сервера и хочешь защитить своё сообщество от фишинга, "
                        + "то можешь добавить этого бота к себе на сервер с помощью ссылки: ").append(inviteUrl);

                try {
                    PrivateChannel channel = event.getMember().getUser().openPrivateChannel().complete();
                    channel.sendMessage(message.toString()).complete();

                    if (event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS))
                        event.getMember().kick("Рассылка фишинговых ссылок").queue();
                } catch (Exception exception) {
                    logger.warn("Unable to notify {} about kick", event.getMember().getUser().getAsTag(), exception);
                }
            }
        } catch (Exception exception) {
            logger.error("Unable to process message", exception);
        }
    }
}
