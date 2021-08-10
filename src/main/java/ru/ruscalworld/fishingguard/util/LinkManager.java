package ru.ruscalworld.fishingguard.util;

import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruscalworld.fishingguard.models.BannedAddress;
import ru.ruscalworld.fishingguard.models.BannedDomain;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LinkManager {
    private static final Logger logger = LoggerFactory.getLogger("LinkManager");

    public static List<URL> parseLinks(String string) {
        List<URL> links = new ArrayList<>();

        for (String word : string.split(" ")) {
            // https://www.urlregex.com
            if (!word.matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) continue;

            try {
                links.add(new URL(word));
            } catch (MalformedURLException ignored) { }
        }

        return links;
    }

    public static boolean checkLink(URL url, Guild guild) {
        // Simply check if domain is blacklisted
        Optional<BannedDomain> domainBan = BannedDomain.getByDomain(url.getHost());
        if (domainBan.isPresent()) return true;

        try {
            // Now check IP address of website
            InetAddress address = InetAddress.getByName(url.getHost());
            Optional<BannedAddress> addressBan = BannedAddress.getByAddress(address);

            // If address is banned, also ban a domain
            if (addressBan.isPresent()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        BannedDomain domain = BannedDomain.banDomain(url.getHost(), guild);
                        logger.info("Banned domain {} because it points at blacklisted IP address ({})", domain.getDomain(), addressBan.get().getAddress());
                    } catch (Exception exception) {
                        logger.error("Failed to ban domain {}", url.getHost(), exception);
                    }
                });

                return true;
            }
        } catch (UnknownHostException ignored) { }
        return false;
    }
}
