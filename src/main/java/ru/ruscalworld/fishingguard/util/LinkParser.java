package ru.ruscalworld.fishingguard.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LinkParser {
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
}
