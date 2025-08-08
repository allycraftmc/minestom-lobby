package de.allycraft.lobby.utils;

public class MapIdManager {
    private int nextId = 0;

    public int nextId() {
        return ++this.nextId;
    }
}
