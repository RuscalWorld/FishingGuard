package ru.ruscalworld.fishingguard.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.fishingguard.FishingGuard;
import ru.ruscalworld.fishingguard.models.BannedDomain;
import ru.ruscalworld.fishingguard.models.Incident;
import ru.ruscalworld.fishingguard.util.LinkManager;
import ru.ruscalworld.storagelib.Storage;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class MessageListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getMember() == null) return;
        List<URL> links = LinkManager.parseLinks(event.getMessage().getContentDisplay());

        for (URL link : links) {
            try {
                if (LinkManager.checkLink(link, event.getGuild())) {
                    event.getMessage().delete().queue();
                    Incident.create(event.getMember(), event.getMessage().getContentRaw());
                }
            } catch (Exception exception) {
                logger.error("Unable to process link {}", link.toString(), exception);
            }
        }
    }
}
