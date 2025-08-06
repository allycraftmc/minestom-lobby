package de.allycraft.lobby;

import net.minestom.server.MinecraftServer;

public class Main {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        minecraftServer.start("0.0.0.0", 25565);
    }
}
