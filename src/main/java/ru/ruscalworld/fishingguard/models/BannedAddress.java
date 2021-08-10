package ru.ruscalworld.fishingguard.models;

import net.dv8tion.jda.api.entities.Guild;
import ru.ruscalworld.fishingguard.FishingGuard;
import ru.ruscalworld.fishingguard.util.WhitelistedIPs;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;
import ru.ruscalworld.storagelib.exceptions.InvalidModelException;
import ru.ruscalworld.storagelib.exceptions.NotFoundException;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Optional;

@Model(table = "ips")
public class BannedAddress extends BannedEntry {
    private static final Storage storage = FishingGuard.getInstance().storage();

    @Property(column = "address")
    private String address;

    public BannedAddress() {

    }

    public static Optional<BannedAddress> getByAddress(InetAddress address) {
        try {
            BannedAddress bannedAddress = storage.find(BannedAddress.class, "address", address.getHostAddress());
            return Optional.ofNullable(bannedAddress);
        } catch (InvalidModelException | SQLException | NotFoundException ignored) { }
        return Optional.empty();
    }

    public static BannedAddress banAddress(InetAddress address, Guild guild) throws InvalidModelException, SQLException, NotFoundException {
        WhitelistedIPs.ensureIsNotWhitelisted(address);
        if (getByAddress(address).isPresent()) return null;
        BannedAddress bannedAddress = new BannedAddress(address.getHostAddress());
        bannedAddress.setGuildID(guild.getId());

        long id = storage.save(bannedAddress);
        return storage.retrieve(BannedAddress.class, id);
    }

    public BannedAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
