package de.allycraft.lobby.spark;

import me.lucko.spark.common.tick.AbstractTickReporter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.server.ServerTickMonitorEvent;

import java.util.UUID;

public class MinestomTickReporter extends AbstractTickReporter {
    private final EventNode<Event> eventNode = EventNode.all("spark-tick-reporter-" + UUID.randomUUID());

    public MinestomTickReporter() {
        this.eventNode.addListener(ServerTickMonitorEvent.class, event -> this.onTick(event.getTickMonitor().getTickTime()));
    }

    @Override
    public void start() {
        MinecraftServer.getGlobalEventHandler().addChild(this.eventNode);
    }

    @Override
    public void close() {
        MinecraftServer.getGlobalEventHandler().removeChild(this.eventNode);
    }
}
