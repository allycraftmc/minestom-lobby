package de.allycraft.lobby.utils;

import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.map.LargeFramebuffer;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.LargeDirectFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.utils.Direction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class LargeMapDisplay {
    private final int width, height; // in maps, not pixels
    private final MapDataPacket[] packets;

    public LargeMapDisplay(MapIdManager idManager, LargeFramebuffer framebuffer) {
        this.width = Math.ceilDiv(framebuffer.width(), 128);
        this.height = Math.ceilDiv(framebuffer.height(), 128);
        this.packets = new MapDataPacket[this.width * this.height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.packets[x + y * this.width] = framebuffer.createSubView(x * 128, y * 128)
                        .preparePacket(idManager.nextId());
            }
        }
    }

    public static LargeMapDisplay fromImage(MapIdManager idManager, Path path) {
        BufferedImage image;
        try {
            image = ImageIO.read(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        LargeDirectFramebuffer framebuffer = new LargeDirectFramebuffer(imageWidth, imageHeight);
        for(int x = 0; x < imageWidth; x++) {
            for(int y = 0; y < imageHeight; y++) {
                int argb = image.getRGB(x, y);
                if((argb >> 24 & 0xFF) == 0) {
                    framebuffer.setMapColor(x, y, MapColors.NONE.baseColor());
                } else {
                    framebuffer.setMapColor(x, y, MapColors.closestColor(argb).getIndex());
                }
            }
        }
        return new LargeMapDisplay(idManager, framebuffer);
    }

    public void sendPackets(Player player) {
        for(MapDataPacket packet : this.packets) {
            player.sendPacket(packet);
        }
    }

    public void spawn(Instance instance, Pos pos, Direction direction) {
        Vec xFlowDirection;
        Vec yFlowDirection;

        xFlowDirection = switch (direction) {
            // Rotate direction by 90° around the Y-Axis
            case NORTH, SOUTH, EAST, WEST -> new Vec(direction.normalZ(), direction.normalY(), -direction.normalX());
            case UP, DOWN -> Direction.EAST.vec();
        };

        yFlowDirection = switch (direction) {
            case NORTH, SOUTH, EAST, WEST -> Direction.DOWN.vec();
            // Rotate direction by 90° around the X-Axis
            case UP, DOWN -> new Vec(direction.normalX(), -direction.normalZ(), direction.normalY());
        };

        for(int x = 0; x < this.width; x++) {
            for(int y = 0; y < this.height; y++) {
                int mapId = this.packets[x + y * this.width].mapId();
                Entity itemFrame = new Entity(EntityType.ITEM_FRAME);
                ItemFrameMeta itemFrameMeta = (ItemFrameMeta) itemFrame.getEntityMeta();
                itemFrameMeta.setNotifyAboutChanges(false);
                itemFrameMeta.setInvisible(true);
                itemFrameMeta.setDirection(direction);
                itemFrameMeta.setItem(ItemStack.of(Material.FILLED_MAP).with(DataComponents.MAP_ID, mapId));
                itemFrameMeta.setNotifyAboutChanges(true);
                itemFrame.setInstance(instance, pos.add(xFlowDirection.mul(x).add(yFlowDirection.mul(y))));
            }
        }
    }
}
