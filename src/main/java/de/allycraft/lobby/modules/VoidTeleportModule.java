package de.allycraft.lobby.modules;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.world.DimensionType;

public class VoidTeleportModule {
    private final Pos spawnPosition;
    private final int minY;

    public VoidTeleportModule(Instance instance, Pos spawnPosition) {
        this.spawnPosition = spawnPosition;
        DimensionType dimensionType = MinecraftServer.getDimensionTypeRegistry().get(instance.getDimensionType());
        this.minY = dimensionType != null ? dimensionType.minY() : -64;
    }

    public void register(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerMoveEvent.class, this::onPlayerMove);
    }

    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getNewPosition().y() < minY) {
            player.teleport(this.spawnPosition);
        }
    }
}
