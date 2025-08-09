package de.allycraft.lobby.spark;

import me.lucko.spark.common.command.sender.AbstractCommandSender;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.entity.Player;

import java.util.UUID;
import java.util.function.BiPredicate;

public class MinestomCommandSender extends AbstractCommandSender<CommandSender> {
    private final BiPredicate<CommandSender, String> permissionHandler;

    public MinestomCommandSender(CommandSender delegate, BiPredicate<CommandSender, String> permissionHandler) {
        super(delegate);
        this.permissionHandler = permissionHandler;
    }

    @Override
    public String getName() {
        if(this.delegate instanceof Player player) {
            return player.getUsername();
        }
        if(this.delegate instanceof ConsoleSender) {
            return "console";
        }
        return "unknown: " + this.delegate.getClass().getSimpleName();
    }

    @Override
    public UUID getUniqueId() {
        if(this.delegate instanceof Player player) {
            return player.getUuid();
        }
        return null;
    }

    @Override
    public void sendMessage(Component component) {
        this.delegate.sendMessage(component);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.permissionHandler.test(this.delegate, permission);
    }
}
