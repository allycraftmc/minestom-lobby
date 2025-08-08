package de.allycraft.lobby.hooks;

import de.allycraft.lobby.utils.PermissionUtils;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerGameModeRequestEvent;
import net.minestom.server.event.player.PlayerPickBlockEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class GamemodeHook {
    private final LuckPerms luckPerms;

    public GamemodeHook(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    public void register(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerGameModeRequestEvent.class, this::onPlayerGameModeRequest);
        eventNode.addListener(PlayerPickBlockEvent.class, this::onPlayerPickBlock);
    }

    private void onPlayerGameModeRequest(PlayerGameModeRequestEvent event) {
        if(!PermissionUtils.hasPermission(this.luckPerms, event.getPlayer(), "allycraft.lobby.gamemmode")) return;
        event.getPlayer().setGameMode(event.getRequestedGameMode());
        event.getPlayer().sendMessage(Component.text("Set own gamemmode to " + event.getRequestedGameMode()));
    }

    private void onPlayerPickBlock(PlayerPickBlockEvent event) {
        Player player = event.getPlayer();
        if(player.getGameMode().equals(GameMode.CREATIVE)) {
            Material material = event.getBlock().registry().material();
            if(material == null) return;
            for(byte slot = 0; slot < 9; slot++) {
                if(player.getInventory().getItemStack(slot).material().equals(material)) {
                    player.setHeldItemSlot(slot);
                    return;
                }
            }
            byte slot = player.getHeldSlot();
            for(byte i = 0; i < 9; i++) {
                if(player.getInventory().getItemStack(i).isAir()) {
                    slot = i;
                    break;
                }
            }
            player.setHeldItemSlot(slot);
            player.getInventory().setItemStack(slot, ItemStack.of(material));
        }
    }
}
