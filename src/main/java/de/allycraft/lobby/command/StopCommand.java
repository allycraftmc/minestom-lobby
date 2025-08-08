package de.allycraft.lobby.command;

import de.allycraft.lobby.utils.PermissionUtils;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class StopCommand extends Command {
    private final LuckPerms luckPerms;

    public StopCommand(LuckPerms luckPerms) {
        super("stop");
        this.luckPerms = luckPerms;

        setCondition((sender, command) -> sender instanceof Player player && PermissionUtils.hasPermission(this.luckPerms, player, "allycraft.lobby.stop")); // TODO permission check
        setDefaultExecutor((sender, command) -> MinecraftServer.stopCleanly());
    }
}
