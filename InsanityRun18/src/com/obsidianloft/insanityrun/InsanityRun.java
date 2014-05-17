/* Insanity Run by Jason Oakley aka WauloK -  a fun "Temple Run" type game for Minecraft Bukkit & Spigot
    Copyright (C) 2014  Jason Oakley

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.obsidianloft.insanityrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.obsidianloft.insanityrun.commands.CommandManager;
import com.obsidianloft.insanityrun.events.ClickSignListener;
import com.obsidianloft.insanityrun.events.CreateSignListener;
import com.obsidianloft.insanityrun.events.PlayerListener;

public final class InsanityRun extends JavaPlugin {
	
	public InsanityRun() {
	    plugin = this;
	  }
	  
	  public static InsanityRun instance() {
	    return plugin;
	  }

	public final Logger logger = Logger.getLogger("Minecraft");

	public static InsanityRun plugin;
	public static String gameName; // Use the game name in code

	public static boolean useVault = false; // Game has not started yet
	public static Economy economy = null; // Set up economy variable
	public static String useLanguage = null; // Get language to use
	public static String currentArena; // Store current arena for creating
	public static String gameVersion; // Store game version
	public static ArrayList<String> helpText = new ArrayList<>(); // Array to hold help text
	public static Integer idleKickTime; // How many idle seconds to kick player out of game
	public static Integer blockJumpHeight; // How far under player to detect blocks
	
	public static Integer idleTaskID; // Task ID for idling players
	public static Boolean broadcastWins; // Broadcast wins?
	public static String broadcastWinsText; // Broadcast wins text output.

	// HashMap contains Value = Player Name, Object = iPlayer object
	public static HashMap<String,iPlayer> playerObject = new HashMap<String,iPlayer>();

	// ArrayList contains list of arenas
	public static ArrayList<String> arenaList = new ArrayList<String>();
	
	// HashMap of number of players in each arena
	public static HashMap<String,Integer> playersInThisArena = new HashMap<String,Integer>();

	// Players to kick for quitting Minecraft. If they come back, send them to start.
	public static HashMap<String,Location> playerQuitList = new HashMap<String,Location>();
	
	// Set up Listeners and Managers
	public static final CreateSignListener createSignListener = new CreateSignListener();
	public static final ClickSignListener clickSignListener = new ClickSignListener();
	public static final PlayerListener playerMoveListener = new PlayerListener();
	public static final CommandManager commandManager = new CommandManager();

	@Override
	public void onEnable(){
		plugin = this;
		// Register our Listeners with the Plugin Manager
		PluginManager plMan = this.getServer().getPluginManager();
		plMan.registerEvents(createSignListener, this);
		plMan.registerEvents(clickSignListener, this);
		plMan.registerEvents(playerMoveListener, this);
		getCommand("irun").setExecutor(new CommandManager());

		// Save a default copy of the config.yml from the InsanityRun.jar file
		this.saveDefaultConfig();
		PluginDescriptionFile pdfFile = this.getDescription();		
		gameVersion = pdfFile.getVersion();		

		// Configure missing config.yml keys
		configureKeys();

		// Set up config global variables
		setupConfigVars();

		// Is the config.yml set up for Vault support?
		if (useVault) {
			this.logger.info(pdfFile.getName() + " " + plugin.getConfig().getString(useLanguage + ".vaultEnabled"));
			// See if Vault is loaded
			if (Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault) {
				this.logger.info(pdfFile.getName() + " " + plugin.getConfig().getString(useLanguage + ".vaultFound"));
				RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

				if(service != null) {
					economy = service.getProvider();
				}


			}
			else {
				this.logger.info(pdfFile.getName() + " " + plugin.getConfig().getString(useLanguage + ".vaultNotFound"));
				useVault = false; // They don't have vault so disable it!
			}
		}
		else {
			this.logger.info(pdfFile.getName() + " " + plugin.getConfig().getString(useLanguage + ".vaultDisabled"));
		}

		// Enable MCStats.com statistics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			this.logger.info(pdfFile.getName() + "InsanityRun: MCStats not enabled");
		}
	}

	@Override
	public void onDisable(){
		this.getServer().getScheduler().cancelAllTasks();
		// Kick players back to the sign click location
		GameManager.serverRestartKick();
	}

	// Configure new/missing config.yml keys
	@SuppressWarnings("unchecked")
	private void configureKeys() {
		// Added new, missing keys after 1.3.
		plugin.getConfig().set("en.vaultAward","Congratulations! You were paid");
		plugin.getConfig().set("en.help1", "== Help for Insanity Run by WauloK == " + " (v" + gameVersion + ")");
		plugin.getConfig().set("en.help8", "/irun sch|setcharge <arena> <amount> - Set charge $<amount> of <arena>");
		plugin.getConfig().set("en.help9", "/irun adj|adjoin <player> <arena> - Make <player> join <arena>");
		plugin.getConfig().set("en.help10", "/irun adl|adleave <player> <arena> - Make <player> leave <arena>");
		plugin.getConfig().set("en.help11", "/irun lnk|linkarenas <arena1> <arena2> - Link <arena1> to <arena2>");
		plugin.getConfig().set("en.help12","/irun list - List arenas");

		// Add key for how far under player to detect block jumped
		if (!plugin.getConfig().contains("blockJumpHeight")) {
			plugin.getConfig().set("blockJumpHeight",0.5F);
		}
				
		this.saveConfig();
	}

	// Read config variables
	public static void setupConfigVars() {
		gameName = plugin.getDescription().getName();
		useVault = plugin.getConfig().getBoolean("useVault");
		useLanguage = plugin.getConfig().getString("useLanguage");
		for (int i = 1;i<13;i++) helpText.add(plugin.getConfig().getString(useLanguage + ".help"+i));
		arenaList = (ArrayList<String>) plugin.getConfig().getStringList("arenaList");
		idleKickTime = plugin.getConfig().getInt("idleKickTime");
		broadcastWins = plugin.getConfig().getBoolean("broadcastWins");
		broadcastWinsText = plugin.getConfig().getString(useLanguage + ".broadcastWinsText");
		blockJumpHeight = plugin.getConfig().getInt("blockJumpHeight");
	}
}
