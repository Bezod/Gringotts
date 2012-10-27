package org.gestern.gringotts;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;


public class Gringotts extends JavaPlugin {

	/** The Gringotts plugin instance. */
	public static Gringotts gringotts;
	
    private PluginManager pluginmanager;
    private Logger log = Bukkit.getServer().getLogger();

    
    private final Commands gcommand = new Commands(this);
    public final AccountHolderFactory accountHolderFactory = new AccountHolderFactory();

    /** Manages accounts. */
    public Accounting accounting;


    @Override
    public void onEnable() {
    	
    	gringotts = this;
    	
        pluginmanager = getServer().getPluginManager();

        CommandExecutor playerCommands = gcommand.new Money();
        CommandExecutor moneyAdminCommands = gcommand.new Moneyadmin();
        CommandExecutor adminCommands = gcommand.new GringottsCmd();

        getCommand("balance").setExecutor(playerCommands);
        getCommand("money").setExecutor(playerCommands);
        getCommand("moneyadmin").setExecutor(moneyAdminCommands);
        getCommand("gringotts").setExecutor(adminCommands);


        // load and init configuration
        saveDefaultConfig(); // saves default configuration if no config.yml exists yet
        FileConfiguration savedConfig = getConfig();
        Configuration.config.readConfig(savedConfig);

        accounting = new Accounting();

        registerEvents();
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
        	log.info("[Gringotts] Failed to submit PluginMetrics stats");
            // Failed to submit the stats :-(
        }
        
        log.info("[Gringotts] enabled");
    }

	@Override
    public void onDisable() {
    	log.info("[Gringotts] shutting down, saving configuration");
    	// fill config file
    	Configuration.config.saveConfig(getConfig());
        
        // shut down db connection
        DAO.getDao().shutdown();
        
        log.info("[Gringotts] disabled");
    }



    private void registerEvents() {
        pluginmanager.registerEvents(new AccountListener(this), this);
    }


    // TODO add optional dependency to factions. how?
}
