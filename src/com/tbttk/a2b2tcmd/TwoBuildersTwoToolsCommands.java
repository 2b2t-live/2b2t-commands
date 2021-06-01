package com.tbttk.a2b2tcmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TwoBuildersTwoToolsCommands extends JavaPlugin implements Listener {

    File configFile;
    FileConfiguration config;
    List<String> blacklist;
    String blacklistMessage;

    @Override
    public void onEnable() {

        configFile = new File(getDataFolder(), "config.yml");

        try {
            firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
        config = new YamlConfiguration();
        loadYamls();

        getServer().getPluginManager().registerEvents(this, this);
        blacklist = config.getStringList("blacklisted-commands");
        blacklistMessage = config.getString("blacklisted-message");

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
    	if (e.getMessage().split(" ")[0].contains(":")) {
	    	  e.setCancelled(true);
	          e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', blacklistMessage));
	      }
    	if(e.getMessage().split(" ")[0].equalsIgnoreCase("/kill")) {
    		e.setCancelled(true);
    		if (e.getPlayer() instanceof Player) {
    			((Player) e.getPlayer()).setHealth(0);
    			Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + e.getPlayer().getName() + ChatColor.DARK_RED + " has been obliterated!");
    		}
    	}
        String command = e.getMessage().replace("/", "");
        Player p = e.getPlayer();
        if (!p.isOp() || !p.hasPermission("2b2t.noblacklist")) {
            if (blacklist.contains(command.split(" ")[0].toLowerCase())) {
            	e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', blacklistMessage));
            }
        }
        if (command.contains("help")) {
        	e.setCancelled(true);
        	e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&3============="));
        	e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Demo Mode"));
        	e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&3============="));
        }
    }
    @EventHandler
    public void onTab(TabCompleteEvent e) {
    	if(!e.getSender().isOp()) {
    		if(!(e.getBuffer().startsWith("/w ") || e.getBuffer().startsWith("/tell "))) {
    		e.setCancelled(true);
    		}
    	}
    }
    private void firstRun() throws Exception {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadYamls() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
