package ru.ruscalworld.fishingguard;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.impl.SQLiteStorage;

import java.nio.file.Path;

public record FishingGuard(Storage storage, JDA jda) {
    private static final Logger logger = LoggerFactory.getLogger("FishingGuard");
    private static FishingGuard instance;

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger("Initializer");

        try {
            String token = System.getenv("FG_BOT_TOKEN");
            if (token == null) {
                logger.error("Bot token must be provided with FG_BOT_TOKEN environment variable");
                return;
            }

            JDABuilder builder = JDABuilder.createDefault(token);
            builder.setActivity(Activity.watching("за вашими серверами"));
            JDA jda = builder.build();

            String databaseURL = System.getenv("FG_DATABASE_URL");
            if (databaseURL == null)
                databaseURL = Path.of(System.getProperty("user.home")).resolve("fishing.db").toString();
            SQLiteStorage storage = new SQLiteStorage("jdbc:sqlite:" + databaseURL);

            new FishingGuard(storage, jda).onStart();
        } catch (Exception exception) {
            logger.error("Bot initialization failed", exception);
        }
    }

    public void onStart() {
        logger.info("Successfully logged in as {}", this.getJDA().getSelfUser().getAsTag());
    }

    public static FishingGuard getInstance() {
        return instance;
    }

    public Storage getStorage() {
        return storage;
    }

    public JDA getJDA() {
        return jda;
    }
}
