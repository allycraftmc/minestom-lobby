package de.allycraft.lobby.utils;

import net.luckperms.api.LuckPerms;
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
}
