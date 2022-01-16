package ru.ruscalworld.fishingguard.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.fishingguard.models.BannedAddress;

import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class EmbedChecker {
    private static final Logger logger = LoggerFactory.getLogger("EmbedChecker");
    private static final List<Predicate<MessageEmbed>> pipeline = List.of(
            // Check for "Discord Steam"
            (embed) -> {
                String title = embed.getTitle();
                if (title == null) return false;
                title = title.toLowerCase(Locale.ROOT);
                return title.contains("discord nitro") && title.contains("steam") && (title.contains("free") || title.contains("бесплатно"));
            },

            // Check for any unofficial "gifts"
            (embed) -> {
                String title = embed.getTitle();
                if (title == null) return false;
                title = title.toLowerCase(Locale.ROOT);

                String description = embed.getDescription();
                if (description == null) return false;
                description = description.toLowerCase(Locale.ROOT);

                return title.contains("gift") && (title.contains("subscription") || title.contains("nitro")) &&
                        description.contains("gift") && (description.contains("subscription") || description.contains("nitro")) &&
                        description.contains("you") && description.contains("month");
            }
    );

    public static boolean checkEmbed(MessageEmbed embed, Guild guild) {
        for (Predicate<MessageEmbed> predicate : pipeline) {
            if (predicate.test(embed)) continue;
            CompletableFuture.runAsync(() -> {
                if (embed.getUrl() == null) return;

                try {
                    URL url = new URL(embed.getUrl());
                    InetAddress address = InetAddress.getByName(url.getHost());
                    BannedAddress bannedAddress = BannedAddress.banAddress(address, guild);
                    if (bannedAddress != null)
                        logger.info("Banned address {} because it was resolved while checking suspicious embed", bannedAddress.getAddress());
                } catch (Exception exception) {
                    logger.error("Unable to ban address", exception);
                }
            });
            return true;
        }
        return false;
    }
}
