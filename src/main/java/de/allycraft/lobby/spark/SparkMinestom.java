package de.allycraft.lobby.spark;

import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.common.util.SparkThreadFactory;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.stream.Stream;

public class SparkMinestom implements SparkPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkMinestom.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new SparkThreadFactory());

    private final Path dataDirectory;
    private final MinestomCommandSenderFactory commandSenderFactory;

    @Nullable
    private SparkPlatform platform;
    @Nullable
    private MinestomSparkCommand command;

    public SparkMinestom(Path dataDirectory, BiPredicate<CommandSender, String> permissionHandler) {
        this.dataDirectory = dataDirectory;
        this.commandSenderFactory = new MinestomCommandSenderFactory(permissionHandler);
    }

    public static Builder builder(Path dataDirectory) {
        return new Builder(dataDirectory);
    }

    public void enable() {
        this.platform = new SparkPlatform(this);
        this.platform.enable();

        this.command = new MinestomSparkCommand(this.platform, this.commandSenderFactory);
        MinecraftServer.getCommandManager().register(this.command);
    }

    public void disable() {
        if (this.platform != null) {
            this.platform.disable();
        }

        if (this.command != null) {
            MinecraftServer.getCommandManager().unregister(this.command);
        }

        this.scheduler.shutdown();
    }

    @Override
    public String getVersion() {
        return "unknown"; // TODO
    }

    @Override
    public Path getPluginDirectory() {
        return this.dataDirectory;
    }

    @Override
    public String getCommandName() {
        return "spark";
    }

    @Override
    public Stream<? extends me.lucko.spark.common.command.sender.CommandSender> getCommandSenders() {
        return Stream.concat(
                    MinecraftServer.getConnectionManager().getOnlinePlayers().stream(),
                    Stream.of(MinecraftServer.getCommandManager().getConsoleSender())
                )
                .map(this.commandSenderFactory::create);
    }

    @Override
    public void executeAsync(Runnable runnable) {
        this.scheduler.execute(runnable);
    }

    @Override
    public PlatformInfo getPlatformInfo() {
        return new MinestomPlatformInfo();
    }

    @Override
    public void log(Level level, String message) {
        if(level.intValue() >= 1000) {
            LOGGER.error(message);
        } else if(level.intValue() >= 900) {
            LOGGER.warn(message);
        } else {
            LOGGER.info(message);
        }
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        if(level.intValue() >= 1000) {
            LOGGER.error(message, throwable);
        } else if(level.intValue() >= 900) {
            LOGGER.warn(message, throwable);
        } else {
            LOGGER.info(message, throwable);
        }
    }

    @Override
    public PlayerPingProvider createPlayerPingProvider() {
        return new MinestomPlayerPingProvider();
    }

    @Override
    public TickReporter createTickReporter() {
        return new MinestomTickReporter();
    }

    @Override
    public TickHook createTickHook() {
        return new MinestomTickHook();
    }

    public static class Builder {
        private final Path dataDirectory;
        private BiPredicate<CommandSender, String> permissionHandler = (sender, permission) -> sender instanceof ConsoleSender;

        private Builder(Path dataDirectory) {
            this.dataDirectory = dataDirectory;
        }

        public Builder permissionHandler(BiPredicate<CommandSender, String> permissionHandler) {
            this.permissionHandler = permissionHandler;
            return this;
        }

        public SparkMinestom enable() {
            SparkMinestom spark = new SparkMinestom(this.dataDirectory, permissionHandler);
            spark.enable();
            return spark;
        }
    }
}
