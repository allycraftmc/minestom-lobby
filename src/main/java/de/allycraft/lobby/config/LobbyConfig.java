package de.allycraft.lobby.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public record LobbyConfig(
        @NotNull Pos spawnPosition,
        List<@NotNull Portal> portals,
        List<@NotNull MapImageDisplay> maps,
        @NotNull AuthMode authMode,
        @NotNull String host,
        int port
        ) {
    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyConfig.class);

    public static LobbyConfig read() {
        try(FileConfig fileConfig = FileConfig.builder("config.toml")
                .defaultResource("/config.toml")
                .autosave()
                .sync()
                .build()
        ) {
            fileConfig.load();

            Pos spawnPosition = readPositionOrElse(fileConfig, "lobby.spawn_pos", new Pos(0, 1, 0));
            List<Config> portalConfigs = fileConfig.getOrElse("lobby.portals", List.of());
            List<Portal> portals = portalConfigs.stream()
                    .map(conf -> {
                        String destination = conf.getOrElse("destination", "");
                        Pos corner1 = readPositionOrElse(conf, "corner1", Pos.ZERO);
                        Pos corner2 = readPositionOrElse(conf, "corner2", Pos.ZERO);
                        return Portal.from(destination, corner1, corner2);
                    })
                    .toList();

            List<Config> mapConfigs = fileConfig.getOrElse("lobby.maps", List.of());
            List<MapImageDisplay> maps = mapConfigs.stream()
                    .map(conf -> {
                        String image = conf.get("image");
                        Pos pos = readPositionOrElse(conf, "pos", Pos.ZERO);
                        Direction direction = switch (conf.getOrElse("direction", "north").toLowerCase()) {
                            case "north" -> Direction.NORTH;
                            case "south" -> Direction.SOUTH;
                            case "west" -> Direction.WEST;
                            case "east" -> Direction.EAST;
                            case "up" -> Direction.UP;
                            case "down" -> Direction.DOWN;
                            case String value -> {
                                LOGGER.error("Unexpected value for map direction: {} (fallback: north)", value);
                                yield Direction.NORTH;
                            }
                        };
                        return new MapImageDisplay(image, pos, direction);
                    })
                    .toList();

            AuthMode authMode = switch (fileConfig.getOrElse("server.auth_mode", "online").toLowerCase()) {
                case "offline" -> AuthMode.OFFLINE;
                case "online" -> AuthMode.ONLINE;
                case "velocity" -> AuthMode.VELOCITY;
                case String value -> {
                    LOGGER.error("Unexpected value for auth mode: {} (fallback: online mode)", value);
                    yield AuthMode.ONLINE;
                }
            };
            String host = fileConfig.getOrElse("server.host", "0.0.0.0");
            int port = fileConfig.getOrElse("server.port", 25565);

            return new LobbyConfig(spawnPosition, portals, maps, authMode, host, port);
        }
    }

    private static Pos readPositionOrElse(Config config, String key, Pos defaultValue) {
        // TODO optional yaw/pitch
        Config pos = config.get(key);
        if (pos == null) {
            return defaultValue;
        }
        double x = pos.<Number>get("x").doubleValue();
        double y = pos.<Number>get("y").doubleValue();
        double z = pos.<Number>get("z").doubleValue();
        return new Pos(x, y, z);
    }

    public record Portal(
            @NotNull String destination,
            @NotNull BlockVec min,
            @NotNull BlockVec max
            ) {
        public static Portal from(@NotNull String destination, @NotNull Pos corner1, @NotNull Pos corner2) {
            return new Portal(
                    destination,
                    new BlockVec(
                            Math.min(corner1.blockX(), corner2.blockX()),
                            Math.min(corner1.blockY(), corner2.blockY()),
                            Math.min(corner1.blockZ(), corner2.blockZ())
                    ),
                    new BlockVec(
                            Math.max(corner1.blockX(), corner2.blockX()),
                            Math.max(corner1.blockY(), corner2.blockY()),
                            Math.max(corner1.blockZ(), corner2.blockZ())
                    )
            );
        }

        public boolean contains(@NotNull Point pos) {
            return min.blockX() <= pos.blockX() && min.blockY() <= pos.blockY() && min.blockZ() <= pos.blockZ()
                    && pos.blockX() <= max.blockX() && pos.blockY() <= max.blockY() && pos.blockZ() <= max.blockZ();
        }
    }

    public record MapImageDisplay(
            @NotNull String image,
            @NotNull Pos pos,
            @NotNull Direction direction
    ) {

    }

    public enum AuthMode {
        OFFLINE,
        ONLINE,
        VELOCITY
    }
}
