package de.allycraft.lobby.modules;

import de.allycraft.lobby.config.LobbyConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PortalModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalModule.class);

    private final LobbyConfig config;
    private static final Tag<Boolean> IN_PORTAL = Tag.Boolean("in_portal");

    public PortalModule(LobbyConfig config) {
        this.config = config;
    }

    public void register(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerMoveEvent.class, this::onPlayerMove);
    }

    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for(LobbyConfig.Portal portal : config.portals()) {
            if(portal.contains(event.getNewPosition())) {
                if(player.getTag(IN_PORTAL)) return;
                player.setTag(IN_PORTAL, true);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
                try {
                    outputStream.writeUTF(portal.destination());
                    outputStream.writeUTF(player.getUuid().toString());
                    player.sendPluginMessage("lobbycore:portal_send", byteArrayOutputStream.toByteArray());
                } catch (IOException e) {
                    LOGGER.error("Could not construct plugin message: ", e);
                    player.sendMessage(Component.text("Internal Error. Consider contacting an admin", NamedTextColor.RED));
                }
                return;
            }
        }
        player.setTag(IN_PORTAL, false);
    }
}
