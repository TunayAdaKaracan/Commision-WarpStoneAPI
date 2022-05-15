package dev.kutuptilkisi.warpstoneapi;

import dev.kutuptilkisi.warpstoneapi.command.PillarCommand;
import dev.kutuptilkisi.warpstoneapi.instance.ArcConfig;
import dev.kutuptilkisi.warpstoneapi.instance.Pillar;
import dev.kutuptilkisi.warpstoneapi.listener.WarpStoneListener;
import dev.kutuptilkisi.warpstoneapi.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class WarpStoneAPI extends JavaPlugin {

    private Database database;

    private ArcConfig messages;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        try {
            setupMessagesConfig();
        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().severe("Cannot Create/Load Messages.yml, Disabling Plugin");
            return;
        }

        database = new Database(this);
        try {
            database.connect();
        } catch (SQLException e) {
            getLogger().severe("Database Config Is Wrong. Disabling Plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            setupDatabase();
        } catch (SQLException e) {
            getLogger().severe("Cant Create Table. Disabling Plugin");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
        if(getConfig().getBoolean("debug")) {
            getCommand("pillar").setExecutor(new PillarCommand(this));
        }
        Bukkit.getPluginManager().registerEvents(new WarpStoneListener(this), this);
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

    private void setupMessagesConfig() throws IOException, InvalidConfigurationException {
        File file = new File(getDataFolder(), "messages.yml");
        if(!file.exists()){
            saveResource("messages.yml", true);
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(file);
        messages = new ArcConfig(yamlConfiguration, file);
    }

    private void setupDatabase() throws SQLException {
        try(Connection connection = database.getHikari().getConnection()){
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE if not exists pillars (ID INT NOT NULL AUTO_INCREMENT, BlockType TEXT, Location TEXT, PRIMARY KEY (ID));");
            ps.executeUpdate();
        }
    }

    public Database getDatabase(){return database;}

    public ArcConfig getMessages() {
        return messages;
    }

    public Pillar makePillar(Location location, Block block){
        String stringLocation = LocationUtil.getStringFromLocation(location);
        String type = block.getType().name();
        try(Connection connection = getDatabase().getHikari().getConnection();) {
            PreparedStatement ps = connection.prepareStatement("insert pillars(BlockType, Location) values(?, ?)");
            ps.setString(1, type);
            ps.setString(2, stringLocation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Pillar(location, block);
    }

    public boolean isPillar(Location location){
        boolean isSame = false;
        String stringLocation = LocationUtil.getStringFromLocation(location);
        try(Connection connection = getDatabase().getHikari().getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from pillars where Location = ?");
            ps.setString(1, stringLocation);
            ResultSet set = ps.executeQuery();
            isSame = set.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSame;
    }

    public void deletePillar(Location location){
        String stringLocation = LocationUtil.getStringFromLocation(location);
        try(Connection connection = getDatabase().getHikari().getConnection();) {
            PreparedStatement ps = connection.prepareStatement("delete from pillars where Location = ?");
            ps.setString(1, stringLocation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Pillar> getPillars(){
        List<Pillar> pillars = new ArrayList<>();
        try(Connection connection = getDatabase().getHikari().getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from pillars");
            ResultSet set = ps.executeQuery();
            while(set.next()){
                Location loc = LocationUtil.getLocationFromString(set.getString("Location"));
                pillars.add(new Pillar(loc, loc.getBlock()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pillars;
    }
}
