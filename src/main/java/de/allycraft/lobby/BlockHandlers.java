package de.allycraft.lobby;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class BlockHandlers {
    public static void register(@NotNull BlockManager blockManager) {
        blockManager.registerHandler(Sign.KEY, Sign::new);
        blockManager.registerHandler(Bed.KEY, Bed::new);
        blockManager.registerHandler(Chest.KEY, Chest::new);
    }

    public static class Sign implements BlockHandler {
        public static final Key KEY = Key.key("minecraft", "sign");
        @Override
        public @NotNull Key getKey() {
            return KEY;
        }

        @Override
        public @NotNull Collection<Tag<?>> getBlockEntityTags() {
            return List.of(
                    Tag.Boolean("is_waxed"),
                    Tag.NBT("front_text"),
                    Tag.NBT("back_text")
            );
        }
    }

    public static class Bed implements BlockHandler {
        public static final Key KEY = Key.key("minecraft", "bed");

        @Override
        public @NotNull Key getKey() {
            return KEY;
        }
    }

    public static class Chest implements BlockHandler {
        public static final Key KEY = Key.key("minecraft", "chest");

        @Override
        public @NotNull Key getKey() {
            return KEY;
        }
    }
}
