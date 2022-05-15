package dev.kutuptilkisi.warpstoneapi.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static Location getLocationFromString(String string){
        String[] array = string.split(":");

        float x, y, z, yaw, pitch;
        x = Float.parseFloat(array[1]);
        y = Float.parseFloat(array[2]);
        z = Float.parseFloat(array[3]);
        yaw = Float.parseFloat(array[4]);
        pitch = Float.parseFloat(array[5]);

        return new Location(Bukkit.getWorld(array[0]), x, y, z, yaw, pitch);
    }

    public static String getStringFromLocation(Location location){
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }


}
