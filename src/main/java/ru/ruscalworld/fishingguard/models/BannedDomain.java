package ru.ruscalworld.fishingguard.models;

import ru.ruscalworld.fishingguard.FishingGuard;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;
import ru.ruscalworld.storagelib.exceptions.InvalidModelException;
import ru.ruscalworld.storagelib.exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.Optional;

@Model(table = "domains")
public class BannedDomain extends BannedEntry {
    private static final Storage storage = FishingGuard.getInstance().storage();

    @Property(column = "domain")
    private String domain;

    public BannedDomain() {

    }

    public BannedDomain(String domain) {
        this.domain = domain;
    }

    public static Optional<BannedDomain> getByDomain(String domain) {
        try {
            BannedDomain bannedDomain = storage.find(BannedDomain.class, "domain", domain);
            return Optional.ofNullable(bannedDomain);
        } catch (InvalidModelException | SQLException | NotFoundException ignored) { }
        return Optional.empty();
    }

    public String getDomain() {
        return domain;
    }
}
