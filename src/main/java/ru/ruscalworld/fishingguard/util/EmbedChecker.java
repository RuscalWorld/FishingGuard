package ru.ruscalworld.fishingguard.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.fishingguard.models.BannedAddress;

import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class EmbedChecker {
    private static final Logger logger = LoggerFactory.getLogger("EmbedChecker");
    private final static HashMap<String, String> TRANSLITERATION_MAP = new HashMap<>() {{
        put("а", "a");
        put("б", "b");
        put("в", "v");
        put("г", "g");
        put("д", "d");
        put("е", "e");
        put("ё", "yo");
        put("ж", "zh");
        put("з", "z");
        put("и", "i");
        put("й", "j");
        put("к", "k");
        put("л", "l");
        put("м", "m");
        put("н", "n");
        put("о", "o");
        put("п", "p");
        put("р", "r");
        put("с", "s");
        put("т", "t");
        put("у", "u");
        put("ф", "f");
        put("х", "x");
        put("ц", "c");
        put("ч", "ch");
        put("ш", "shh");
        put("щ", "y");
        put("ь", "");
        put("э", "e");
        put("ю", "yu");
        put("я", "ya");
    }};

    private static final List<Predicate<MessageEmbed>> pipeline = List.of(
            // Check for "Discord Steam"
            (embed) -> {
                String title = embed.getTitle();
                if (title == null) return false;
                title = title.toLowerCase(Locale.ROOT);
                return title.contains("discord-nitro") && title.contains("steam") && (title.contains("free") || title.contains("бесплатно"));
            },

            // Check for any unofficial "gifts"
            (embed) -> {
                String title = embed.getTitle();
                if (title == null) return false;
                title = normalize(title);

                String description = embed.getDescription();
                if (description == null) return false;
                description = normalize(description);

                return title.contains("gift") && (title.contains("subscription") || title.contains("nitro")) &&
                        description.contains("gift") && (description.contains("subscription") || description.contains("nitro")) &&
                        description.contains("you") && description.contains("month");
            }
    );

    public static boolean checkEmbed(MessageEmbed embed, Guild guild) {
        for (Predicate<MessageEmbed> predicate : pipeline) {
            if (!predicate.test(embed)) continue;
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

    public static String normalize(String input) {
        input = input.replace(" ", "-");
        input = input.toLowerCase(Locale.ROOT);
        input = transliterate(input);
        return input.replaceAll("[^a-z0-9-]+", "");
    }

    public static String transliterate(String input) {
        for (String c : TRANSLITERATION_MAP.keySet()) {
            input = input.replace(c, TRANSLITERATION_MAP.get(c));
        }

        return input;
    }
}
