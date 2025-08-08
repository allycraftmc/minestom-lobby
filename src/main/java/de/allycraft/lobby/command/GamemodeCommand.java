package de.allycraft.lobby.command;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class GamemodeCommand extends Command {
    public GamemodeCommand() {
        super("gamemode");
        setCondition((sender, command) -> sender instanceof Player); // TODO permission check

        setDefaultExecutor((sender, command) -> {
            sender.sendMessage(Component.text("Usage: /gamemmode <gamemode> [targets]"));
        });

        // TODO implement
    }
}
