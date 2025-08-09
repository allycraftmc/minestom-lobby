package de.allycraft.lobby.modules;

import net.kyori.adventure.text.Component;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;

public class ChatLoggerModule {
    public void register(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerChatEvent.class, this::onChatMessage);
    }

    private void onChatMessage(PlayerChatEvent event) {
        Audiences.console().sendMessage(
                Component.text("<")
                        .append(event.getPlayer().getName())
                        .append(Component.text("> "))
                        .append(Component.text(event.getRawMessage()))
        );
    }
}
