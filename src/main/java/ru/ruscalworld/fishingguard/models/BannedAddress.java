package ru.ruscalworld.fishingguard.models;

import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;

@Model(table = "ips")
public class BannedAddress extends BannedEntry {
    @Property(column = "address")
    private String address;

    public BannedAddress() {

    }

    public BannedAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
