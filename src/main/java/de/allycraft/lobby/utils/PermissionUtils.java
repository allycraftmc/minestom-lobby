package de.allycraft.lobby.utils;

import net.luckperms.api.LuckPerms;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.entity.Player;

public class PermissionUtils {
    public static boolean hasPermission(LuckPerms luckPerms, Player player, String permission) {
        return luckPerms.getPlayerAdapter(Player.class)
                .getUser(player)
                .getCachedData()
                .getPermissionData()
                .checkPermission(permission)
                .asBoolean();
    }

    public static boolean hasPermission(LuckPerms luckPerms, CommandSender sender, String permission) {
        if(sender instanceof Player player) {
            return hasPermission(luckPerms, player, permission);
        }
        return sender instanceof ConsoleSender;
    }
}
