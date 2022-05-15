package dev.kutuptilkisi.warpstoneapi;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;

public class Database {

    private final WarpStoneAPI main;

    private HikariDataSource hikari;

    public Database(WarpStoneAPI main){
        this.main = main;
    }

    public void connect() throws SQLException {
        ConfigurationSection section = main.getConfig().getConfigurationSection("MySql");
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", section.getString("host"));
        hikari.addDataSourceProperty("port", section.getInt("port"));
        hikari.addDataSourceProperty("databaseName", section.getString("database-name"));
        hikari.addDataSourceProperty("user", section.getString("username"));
        hikari.addDataSourceProperty("password", section.getString("password"));

    }

    public boolean isConnected(){
        return hikari != null;
    }

    public void disconnect(){
        if(isConnected()){
            hikari.close();
        }
    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}
