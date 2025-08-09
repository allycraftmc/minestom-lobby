package de.allycraft.lobby.spark;

import me.lucko.spark.common.tick.AbstractTickHook;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;

public class MinestomTickHook extends AbstractTickHook {
    @Nullable
    private Task task;

    @Override
    public void start() {
        this.task = MinecraftServer.getSchedulerManager()
                .scheduleTask(this::onTick, TaskSchedule.tick(1), TaskSchedule.tick(1));
    }

    @Override
    public void close() {
        if(this.task != null) {
            this.task.cancel();
        }
    }
}
