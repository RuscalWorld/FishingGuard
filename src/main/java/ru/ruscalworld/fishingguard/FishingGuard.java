package ru.ruscalworld.fishingguard;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.fishingguard.listeners.MessageListener;
import ru.ruscalworld.fishingguard.models.BannedDomain;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.exceptions.InvalidModelException;
import ru.ruscalworld.storagelib.impl.SQLiteStorage;

import java.nio.file.Path;
import java.sql.SQLException;

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
            builder.addEventListeners(new MessageListener());
            JDA jda = builder.build();

            String databaseURL = System.getenv("FG_DATABASE_URL");
            if (databaseURL == null)
                databaseURL = Path.of(System.getProperty("user.home")).resolve("fishing.db").toString();

            SQLiteStorage storage = new SQLiteStorage("jdbc:sqlite:" + databaseURL);
            storage.registerMigration("incidents");
            storage.registerMigration("guilds");
            storage.registerMigration("domains");
            storage.registerMigration("ips");
            storage.actualizeStorageSchema();

            instance = new FishingGuard(storage, jda);
            instance.onStart();
        } catch (Exception exception) {
            logger.error("Bot initialization failed", exception);
            if (instance != null) instance.shutdown();
        }
    }

    public void onStart() throws InvalidModelException, SQLException {
        logger.info("Successfully logged in as {}", this.jda().getSelfUser().getAsTag());
        storage.save(new BannedDomain("discorcl.link"));
    }

    public void shutdown() {
        this.jda().shutdown();
        System.exit(0);
    }

    public static FishingGuard getInstance() {
        return instance;
    }
}
