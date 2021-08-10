package ru.ruscalworld.fishingguard.models;

import ru.ruscalworld.fishingguard.FishingGuard;
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

    public BannedAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
