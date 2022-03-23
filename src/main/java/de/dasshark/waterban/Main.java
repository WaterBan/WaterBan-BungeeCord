package de.dasshark.waterban;

import de.dasshark.waterban.commands.banCommand;
import de.dasshark.waterban.commands.kickCommand;
import de.dasshark.waterban.listeners.ConnectListener;
import de.dasshark.waterban.mysql.MySQL;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class Main extends Plugin {

    private static Main instance;
    private File file;
    private Configuration configuration;
    public String prefix = "§0[§3Water§4BAN§0] §8";

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        load();
        getProxy().getPluginManager().registerCommand(this, new kickCommand());
        getProxy().getPluginManager().registerCommand(this, new banCommand());
        getProxy().getPluginManager().registerListener(this, new ConnectListener());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MySQL.disconnect();
    }

    public static Main getInstance() {
        return instance;
    }

    public Configuration getCfg() {
        return configuration;
    }

    private void load() {
        file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MySQL.username = getCfg().getString("MySQL.Username");
        MySQL.password = getCfg().getString("MySQL.Password");
        MySQL.database = getCfg().getString("MySQL.Database");
        MySQL.host = getCfg().getString("MySQL.Host");
        MySQL.port = getCfg().getString("MySQL.Port");
        MySQL.connect();
    }
}
