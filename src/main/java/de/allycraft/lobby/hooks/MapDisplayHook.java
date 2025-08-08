package de.allycraft.lobby.hooks;

import de.allycraft.lobby.config.LobbyConfig;
import de.allycraft.lobby.utils.LargeMapDisplay;
import de.allycraft.lobby.utils.MapIdManager;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MapDisplayHook {
    private final List<LargeMapDisplay> maps;

    public MapDisplayHook(Instance instance, List<LobbyConfig.MapConfig> mapConfigs, MapIdManager mapIdManager) {
        this.maps = new ArrayList<>();
        for(LobbyConfig.MapConfig mapConfig : mapConfigs) {
            LargeMapDisplay map = LargeMapDisplay.fromImage(mapIdManager, Path.of(mapConfig.image()));
            map.spawn(instance, mapConfig.pos(), mapConfig.direction());
            this.maps.add(map);
        }
    }

    public void register(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            if(event.isFirstSpawn()) {
                for(LargeMapDisplay map : maps) {
                    map.sendPackets(event.getPlayer());
                }
            }
        });
    }
}
