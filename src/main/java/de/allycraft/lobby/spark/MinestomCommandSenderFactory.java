package de.allycraft.lobby.spark;

import net.minestom.server.command.CommandSender;

import java.util.function.BiPredicate;

public class MinestomCommandSenderFactory {
    private final BiPredicate<CommandSender, String> permissionHandler;

    public MinestomCommandSenderFactory(BiPredicate<CommandSender, String> permissionHandler) {
        this.permissionHandler = permissionHandler;
    }

    public me.lucko.spark.common.command.sender.CommandSender create(CommandSender sender) {
        return new MinestomCommandSender(sender, this.permissionHandler);
    }
}
