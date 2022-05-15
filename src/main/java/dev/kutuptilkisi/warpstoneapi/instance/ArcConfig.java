package dev.kutuptilkisi.warpstoneapi.instance;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ArcConfig {
    private final YamlConfiguration config;
    private final File file;

    public ArcConfig(YamlConfiguration config, File file){
        this.config = config;
        this.file = file;
    }

    public String getColorString(String path){
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
