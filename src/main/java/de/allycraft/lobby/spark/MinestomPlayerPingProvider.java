package de.allycraft.lobby.spark;

import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MinestomPlayerPingProvider implements PlayerPingProvider {
    @Override
    public Map<String, Integer> poll() {
        HashMap<String, Integer> playerLatencies = new HashMap<>();
        for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            playerLatencies.put(player.getUsername(), player.getLatency());
        }
        return playerLatencies;
    }
}
