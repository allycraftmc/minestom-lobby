package de.allycraft.lobby.spark;

import me.lucko.spark.common.SparkPlatform;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class MinestomSparkCommand extends Command {
    public MinestomSparkCommand(SparkPlatform platform, MinestomCommandSenderFactory commandSenderFactory) {
        super(platform.getPlugin().getCommandName());

        this.setCondition((sender, command) -> platform.hasPermissionForAnyCommand(commandSenderFactory.create(sender)));
        this.setDefaultExecutor((sender, context) -> platform.executeCommand(
                commandSenderFactory.create(sender), new String[0]
        ));

        ArgumentStringArray args = ArgumentType.StringArray("args");
        args.setSuggestionCallback((sender, context, suggestion) -> {
            platform.tabCompleteCommand(commandSenderFactory.create(sender), context.get(args))
                    .forEach(suggestionEntry -> {
                        suggestion.addEntry(new SuggestionEntry(suggestionEntry));
                    });
        });

        this.addSyntax((sender, context) -> {
            platform.executeCommand(commandSenderFactory.create(sender), context.get(args));
        }, args);
    }
}
