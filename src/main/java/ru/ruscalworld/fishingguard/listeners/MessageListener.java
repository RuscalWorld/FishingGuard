package ru.ruscalworld.fishingguard.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.fishingguard.models.BannedDomain;
import ru.ruscalworld.fishingguard.util.LinkParser;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        List<URL> links = LinkParser.parseLinks(event.getMessage().getContentDisplay());
        for (URL link : links) {
            Optional<BannedDomain> ban = BannedDomain.getByDomain(link.getHost());
            if (ban.isPresent()) event.getMessage().addReaction("❗").queue();
        }
    }
}
