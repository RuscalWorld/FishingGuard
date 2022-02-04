package ru.ruscalworld.fishingguard.util;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkManagerTest {
    @Test
    void parseLinks() throws MalformedURLException {
        List<URL> links = LinkManager.parseLinks("Lorem ipsum dolor https://google.com sit amet, consectetur\nhttp://discord.com adipiscing elit...");
        assertEquals(2, links.size());
        assertTrue(links.containsAll(List.of(new URL("https://google.com"), new URL("http://discord.com"))));
    }
}