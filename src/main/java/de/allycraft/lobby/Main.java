package de.allycraft.lobby;

import de.allycraft.lobby.command.GamemodeCommand;
import de.allycraft.lobby.command.StopCommand;
import de.allycraft.lobby.config.LobbyConfig;
import de.allycraft.lobby.modules.*;
import de.allycraft.lobby.luckperms.HoconConfigurationAdapter;
import de.allycraft.lobby.utils.LargeMapDisplay;
import de.allycraft.lobby.utils.MapIdManager;
import de.allycraft.lobby.utils.PermissionUtils;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.*;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        BlockHandlers.register(MinecraftServer.getBlockManager());

        LobbyConfig config = LobbyConfig.read();

        switch (config.authMode()) {
            case OFFLINE -> LOGGER.warn("Offline Mode is enabled. DO NOT USE IN PRODUCTION ENVIRONMENTS");
            case ONLINE -> MojangAuth.init();
            case VELOCITY -> {
                String velocitySecret = System.getenv("VELOCITY_SECRET");
                if(velocitySecret == null || velocitySecret.isEmpty()) {
                    throw new RuntimeException("VELOCITY_SECRET environment variable must be set");
                }
                VelocityProxy.enable(velocitySecret);
            }
        }

        LuckPerms luckPerms = LuckPermsMinestom.builder(Path.of("luckperms"))
                .commandRegistry(CommandRegistry.minestom())
                .configurationAdapter(HoconConfigurationAdapter::new)
                .permissionSuggestions("allycraft.lobby.admin", "allycraft.lobby.gamemode", "allycraft.lobby.stop")
                .enable();

        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new GamemodeCommand());
        commandManager.register(new StopCommand(luckPerms));
        commandManager.setUnknownCommandCallback((sender, command) -> {
            sender.sendMessage(Component.text("Unknown command", NamedTextColor.RED));
        });

        // TODO support polar world format
        InstanceContainer instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(
                new AnvilLoader("world")
        );
        instanceContainer.setTime(6000);
        instanceContainer.setTimeRate(0);

        MapIdManager mapIdManager = new MapIdManager();
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

        if(config.authMode() == LobbyConfig.AuthMode.VELOCITY) {
            new PortalModule(config).register(eventHandler);
        } else {
            LOGGER.info("Portals are disabled in non-velocity auth modes");
        }
        new LobbyGuardModule().register(eventHandler);
        new GamemodeModule(luckPerms).register(eventHandler);
        new VoidTeleportModule(instanceContainer, config.spawnPosition()).register(eventHandler);
        new MapDisplayModule(instanceContainer, config.maps(), mapIdManager).register(eventHandler);

        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(config.spawnPosition());
            player.setGameMode(GameMode.ADVENTURE);

            if(PermissionUtils.hasPermission(luckPerms, player, "allycraft.lobby.admin")) {
                player.setPermissionLevel(4);
            }
        });

        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            if(event.isFirstSpawn()) {
                Audiences.server().sendMessage(
                        event.getPlayer().getName().color(NamedTextColor.RED)
                                .append(Component.text(" joined the game", NamedTextColor.YELLOW))
                );
            }
        });

        eventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            Audiences.server().sendMessage(
                    event.getPlayer().getName().color(NamedTextColor.RED)
                            .append(Component.text(" left the game", NamedTextColor.YELLOW))
            );
        });

        MinecraftServer.getSchedulerManager().buildShutdownTask(LuckPermsMinestom::disable);

        minecraftServer.start(config.host(), config.port());
    }
}
