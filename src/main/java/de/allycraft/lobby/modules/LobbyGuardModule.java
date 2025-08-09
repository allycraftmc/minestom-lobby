package de.allycraft.lobby.modules;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class LobbyGuardModule {
    public void register(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerBlockBreakEvent.class, event -> event.setCancelled(true));
        eventNode.addListener(PlayerBlockPlaceEvent.class, event -> event.setCancelled(true));
    }
}
