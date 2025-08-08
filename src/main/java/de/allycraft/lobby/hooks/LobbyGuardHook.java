package de.allycraft.lobby.hooks;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class LobbyGuardHook {
    public void register(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerBlockBreakEvent.class, event -> event.setCancelled(true));
        eventNode.addListener(PlayerBlockPlaceEvent.class, event -> event.setCancelled(true));
    }
}
