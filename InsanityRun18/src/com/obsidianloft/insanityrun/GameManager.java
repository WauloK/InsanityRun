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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.obsidianloft.insanityrun.InsanityRun;
import com.obsidianloft.insanityrun.events.TaskManager;

public class GameManager {

	public static void showHelp(Player player) {
		// Display help to player
		player.sendMessage(ChatColor.AQUA + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".help1"));
		for (int i = 2;i<4;i++) player.sendMessage(ChatColor.YELLOW + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".help"+i));
		if (player.hasPermission("insanityrun.create")) {
			for (int i = 4;i<13;i++) player.sendMessage(ChatColor.GOLD + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".help"+i));
		}
	}

	public static void joinGame(Player player, String arenaName) {
		String playerName = player.getName();
		iPlayer newPlayerObject = new iPlayer();
		if (!player.hasPermission("insanityrun.join")) {
			player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " join");
			return;
		}

		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null || InsanityRun.plugin.getConfig().getString(arenaName + ".world").equals("")) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return;
		}

		// Does player have enough money to play?
		if (InsanityRun.useVault) {
			if (InsanityRun.economy.getBalance(player.getName()) < InsanityRun.plugin.getConfig().getInt(arenaName + ".charge")) {
				player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".notEnoughMoneyText") + InsanityRun.plugin.getConfig().getInt(arenaName + ".charge") + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".payCurrency"));
				return;
			}
			else {
				// Withdraw money
				EconomyResponse r = InsanityRun.economy.withdrawPlayer(player.getName(), InsanityRun.plugin.getConfig().getInt(arenaName + ".charge"));
				if(r.transactionSuccess()) {
					//player.sendMessage(String.format("You were charged %s %s to play and now have %s %s", r.amount, InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".payCurrency"), r.balance, InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".payCurrency")));
				} else {
					player.sendMessage(String.format("An error occured: %s", r.errorMessage));
				}
			}
		}

		// Player does have permission to play
		player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".readyToPlay"));

		// Construct new player object
		newPlayerObject.setPlayerName(playerName);
		newPlayerObject.setCoins(0);
		newPlayerObject.setInGame(true);
		newPlayerObject.setInArena(arenaName);
		newPlayerObject.setFrozen(false);
		newPlayerObject.setSignClickLoc(player.getLocation());
		InsanityRun.playerObject.put(playerName, newPlayerObject);

		// Start repeating task if first player joined
		if (InsanityRun.playerObject.size() == 1) {
			InsanityRun.idleTaskID = InsanityRun.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(InsanityRun.plugin, new TaskManager(), 20L, 20L); // Every second
			InsanityRun.playersInThisArena.put(arenaName, 0);
		}
		// Read arena teleport location and send player there
		teleportToSpawn(player, arenaName);
		updatePlayerXYZ(player);
		newPlayerObject.setArenaWorld((String) InsanityRun.plugin.getConfig().get(arenaName + ".world"));
		newPlayerObject.setLastMovedTime(System.currentTimeMillis());
		newPlayerObject.setStartRaceTime(System.currentTimeMillis());
		newPlayerObject.setIdleCount(0);
		player.setGameMode(GameMode.SURVIVAL);
		
		int playCount = InsanityRun.playersInThisArena.get(arenaName);
		playCount++;
		InsanityRun.playersInThisArena.put(arenaName, playCount);

		// Update join game sign (if any)
		updateJoinSign(arenaName);
	}

	public static void leaveGame(Player player,String arenaName, iPlayer currentPlayerObject) {
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return;
		}		
		if (!player.hasPermission("insanityrun.leave")) {
			player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noCmdPerms") + " leave");
			return;
		}
		// Are they actually in a game?
		if (currentPlayerObject != null) {
			if (currentPlayerObject.getInGame()) {
				player.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".haveNowLeft") + " " + arenaName);
				currentPlayerObject.setInGame(false);
				player.teleport(currentPlayerObject.getSignClickLoc());
				GameManager.gameOver(player, currentPlayerObject.getInArena(), currentPlayerObject);
				return;
			}
		}
		else {
			player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".haveNotJoined"));
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public static void createArena(Player player, String arenaName) {
		InsanityRun.currentArena = arenaName;
		InsanityRun.arenaList.add(arenaName);
		InsanityRun.plugin.getConfig().set(arenaName + ".world", "");
		InsanityRun.plugin.getConfig().set(arenaName + ".x", 0.0);
		InsanityRun.plugin.getConfig().set(arenaName + ".y", 0.0);
		InsanityRun.plugin.getConfig().set(arenaName + ".z", 0.0);
		InsanityRun.plugin.getConfig().set(arenaName + ".pitch", 0.0);
		InsanityRun.plugin.getConfig().set(arenaName + ".yaw", 0.0);
		InsanityRun.plugin.getConfig().set(arenaName + ".pay", 0);
		InsanityRun.plugin.getConfig().set(arenaName + ".charge", 0);
		InsanityRun.plugin.getConfig().set("arenaList", Arrays.asList(InsanityRun.arenaList));
		InsanityRun.plugin.saveConfig();
		player.sendMessage(ChatColor.AQUA + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".createdArena") + " " + arenaName);
	}

	public static void deleteArena(Player player, String arenaName) {
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return;
		}
		InsanityRun.plugin.getConfig().set(arenaName, null);
		InsanityRun.arenaList.remove(arenaName);
		player.sendMessage(ChatColor.AQUA + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".arenaDeleted") + " " + arenaName);	
		InsanityRun.plugin.saveConfig();
	}

	public static void setSpawnArena(Player player, String arenaName) {
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return;
		}		
		// Get player location and save to config.yml
		Location playerLocation = player.getLocation();
		String world = playerLocation.getWorld().getName();
		double x = playerLocation.getX();
		double y = playerLocation.getY();
		double z = playerLocation.getZ();
		float pitch = playerLocation.getPitch();
		float yaw = playerLocation.getYaw();
		// Save all the details to config.yml
		InsanityRun.plugin.getConfig().set(arenaName + ".world", world);
		InsanityRun.plugin.getConfig().set(arenaName + ".x", x);
		InsanityRun.plugin.getConfig().set(arenaName + ".y", y);
		InsanityRun.plugin.getConfig().set(arenaName + ".z", z);
		InsanityRun.plugin.getConfig().set(arenaName + ".pitch", pitch);
		InsanityRun.plugin.getConfig().set(arenaName + ".yaw", yaw);
		InsanityRun.plugin.saveConfig();
		player.sendMessage(ChatColor.AQUA + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".setSpawnFor") + " " + arenaName);		
	}

	public static void teleportToSpawn(Player player, String arenaName) {
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return;
		}		
		// Get arena spawn location from config.yml
		World world = InsanityRun.plugin.getServer().getWorld((String) InsanityRun.plugin.getConfig().get(arenaName + ".world"));
		double x = InsanityRun.plugin.getConfig().getDouble(arenaName + ".x");
		double y = InsanityRun.plugin.getConfig().getDouble(arenaName + ".y");
		double z = InsanityRun.plugin.getConfig().getDouble(arenaName + ".z");
		float pitch = (float) InsanityRun.plugin.getConfig().getDouble(arenaName + ".pitch");
		float yaw = (float) InsanityRun.plugin.getConfig().getDouble(arenaName + ".yaw");
		// Set new location from config
		player.teleport(new Location(world, x , y, z, yaw, pitch));
	}

	public static void setPayArena(Player player, String arenaName, int pay) {
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return;
		}
		InsanityRun.plugin.getConfig().set(arenaName + ".pay", pay);
		InsanityRun.plugin.saveConfig();
		player.sendMessage(ChatColor.AQUA + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".setPayTo") + " " + pay + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".payCurrency"));	
	}
	public static int getPayArena(Player player, String arenaName) {
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return 0;
		}
		int pay = InsanityRun.plugin.getConfig().getInt(arenaName + ".pay");
		InsanityRun.plugin.saveConfig();
		return pay;
	}

	public static void setChargeArena(Player player, String arenaName, int charge) {
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".world") == null) {
			player.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".noArena"));
			return;
		}
		InsanityRun.plugin.getConfig().set(arenaName + ".charge", charge);
		InsanityRun.plugin.saveConfig();
		player.sendMessage(ChatColor.AQUA + arenaName + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".setChargeTo") + " " + charge + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".payCurrency"));	
	}

	public static void linkArenas(Player player, String arenaName1, String arenaName2) {
		InsanityRun.plugin.getConfig().set(arenaName1 + ".link", arenaName2);
		InsanityRun.plugin.saveConfig();
		player.sendMessage(ChatColor.AQUA + arenaName1 + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".linkedTo") + " " + arenaName2);
	}

	public static void gameOver(Player player, String arenaName, iPlayer currentPlayerObject) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		gameOver2(player, arenaName);
	}

	public static void gameOver2(Player player, String arenaName) {
		String playerName = player.getName();
		player.setFireTicks(0);
		InsanityRun.playerObject.remove(playerName);		
		if (InsanityRun.playerObject.size()==0) {
			InsanityRun.plugin.getServer().getScheduler().cancelTask(InsanityRun.idleTaskID);
		}
		int playCount = InsanityRun.playersInThisArena.get(arenaName);
		playCount--;
		InsanityRun.playersInThisArena.put(arenaName, playCount);
		GameManager.updateJoinSign(arenaName);
		updateJoinSign(arenaName);
	}

	public static void updateJoinSign(String arenaName) {
		// Update join game sign (if any)
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".sign.world") == null) return;
		World world = InsanityRun.plugin.getServer().getWorld((String) InsanityRun.plugin.getConfig().get(arenaName + ".sign.world"));
		double x = InsanityRun.plugin.getConfig().getDouble(arenaName + ".sign.x");
		double y = InsanityRun.plugin.getConfig().getDouble(arenaName + ".sign.y");
		double z = InsanityRun.plugin.getConfig().getDouble(arenaName + ".sign.z");
		signUpdate(new Location(world, x , y, z),arenaName);
	}
	// Actual sign updating code
	public static void signUpdate(Location bLocation, String arenaName) {
		World w = bLocation.getWorld();
		Material bm = w.getBlockAt(bLocation).getType();
		Block b = w.getBlockAt(bLocation);
		if(bm == Material.SIGN_POST|| bm == Material.WALL_SIGN) {
			int inArenaCount=InsanityRun.playersInThisArena.get(arenaName);
			Sign sign = (Sign) b.getState();
			sign.setLine(3, ChatColor.AQUA + "Players: " + inArenaCount);
			sign.update(); // Update sign
		}
	}
	public static void setSignSpawn(Player player, String arenaName) {	
		// Get player location and save to config.yml
		Location playerLocation = player.getLocation();
		String playerName=player.getName();
		final iPlayer currentPlayerObject = InsanityRun.playerObject.get(playerName);
		currentPlayerObject.setSignClickLoc(playerLocation);
	}
	
	// When server restarts, kick all players to spawn
	public static void serverRestartKick() {
		ArrayList<String> playersToKick = new ArrayList<String>();
		Iterator<String> iterator = InsanityRun.playerObject.keySet().iterator();
		while(iterator.hasNext()) {
			String playerName = iterator.next();
			playersToKick.add(playerName);
		}
		// If players in idlekick list, kick them
		for (String idlePlayers:playersToKick) {
			Player player = InsanityRun.plugin.getServer().getPlayer(idlePlayers);
			iPlayer playerObject = InsanityRun.playerObject.get(idlePlayers);
			player.getInventory().setHelmet(playerObject.getHelmetWorn());
			for (PotionEffect effect : player.getActivePotionEffects())
				player.removePotionEffect(effect.getType());
			player.teleport(playerObject.getSignClickLoc());
		}
	}
	
	// Refund money if kicked
	public static void refundMoney(String arenaName,String playerName) {		
		if (InsanityRun.useVault && InsanityRun.plugin.getConfig().getInt(arenaName + ".charge") > 0) {
			EconomyResponse res = InsanityRun.economy.depositPlayer(playerName, InsanityRun.plugin.getConfig().getInt(arenaName + ".charge"));
			if(!res.transactionSuccess()) {
				Bukkit.getConsoleSender().sendMessage(String.format(InsanityRun.gameName + " Vault Deposit - An error occured: %s", res.errorMessage));
			}
		}		
	}
	
	// Update player's XYZ if game teleported
	public static void updatePlayerXYZ(Player player) {
		iPlayer playerObject = InsanityRun.playerObject.get(player.getName());
		Location playerLocation = player.getLocation();
		playerObject.setLastX((int)playerLocation.getX());
		playerObject.setLastY((int)playerLocation.getY());
		playerObject.setLastZ((int)playerLocation.getZ());
		playerObject.setIdleX((int)playerLocation.getX());
		playerObject.setIdleZ((int)playerLocation.getZ());
	}
}