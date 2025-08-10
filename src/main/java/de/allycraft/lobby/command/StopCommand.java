package de.allycraft.lobby.command;

import de.allycraft.minestom.perms.Permissions;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");

        setCondition((sender, command) -> sender instanceof Player player && Permissions.check(player, "allycraft.lobby.stop"));
        setDefaultExecutor((sender, command) -> MinecraftServer.stopCleanly());
    }
}
