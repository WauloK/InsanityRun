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

package com.obsidianloft.insanityrun.events;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.obsidianloft.insanityrun.GameManager;
import com.obsidianloft.insanityrun.InsanityRun;
import com.obsidianloft.insanityrun.iPlayer;

public class PlayerListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		final iPlayer currentPlayerObject = InsanityRun.playerObject.get(playerName);
		int coinsOwned;

		// Is the player in the game?
		if (currentPlayerObject != null) {
			if (!currentPlayerObject.getInGame()) return;

			// Player frozen?
			if (currentPlayerObject.getFrozen()) {
				player.getPlayer().teleport(player.getPlayer().getLocation());
			}

			Location loc = player.getPlayer().getLocation();
			int lastX,lastY,lastZ;
			int locX,locY,locZ;
			// Get player's last X,Y,Z
			lastX=currentPlayerObject.getLastX();
			lastY=currentPlayerObject.getLastY();
			lastZ=currentPlayerObject.getLastZ();
			// Get player's current X,Y,Z
			locX=(int) loc.getX();
			locY=(int) loc.getY();
			locZ=(int) loc.getZ();
			if ((locX != lastX) || (locY != lastY) || (locZ != lastZ)) {
				currentPlayerObject.setLastX(locX);
				currentPlayerObject.setLastY(locY);
				currentPlayerObject.setLastZ(locZ);
				loc.setY(loc.getY() - 0.5F); // Block under feet

				Material blockOn = loc.getWorld().getBlockAt(loc).getType();
				int potionDuration = 20 * 2;
				// Player effects when walking on blocks
				switch(blockOn) {
				case GOLD_BLOCK: // Gold block
					// Add to player's arraylist if it's not already walked on
					ArrayList<Point> playerGoldWalked = currentPlayerObject.getGoldWalkedArray();
					if (!playerGoldWalked.contains(new Point(locX,locZ))) {
						playerGoldWalked.add(new Point(locX,locZ));
						currentPlayerObject.setGoldWalkedArray(playerGoldWalked);
						player.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
						coinsOwned = currentPlayerObject.getCoins();
						coinsOwned++;
						currentPlayerObject.setCoins(coinsOwned);
					}
					break;
				case DIAMOND_BLOCK: // Diamond block
					player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
					player.setVelocity(player.getVelocity().setY(1.5));
					break;
				case SAND: // Sand
				case GRAVEL: // Gravel
					player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 1, 1);
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, potionDuration*2,2));
					break;
				case EMERALD_BLOCK: // Emerald
					player.getWorld().playSound(player.getLocation(), Sound.GLASS, 1, 1);
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, potionDuration*2,2));	
					break;
				case LAPIS_BLOCK: // Lapis
					player.getWorld().playSound(player.getLocation(), Sound.BURP, 1, 1);
					player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, potionDuration*5,0));					
					break;
				case COAL_BLOCK: // Coal
					player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, potionDuration*2,2));
					break;
				case OBSIDIAN: // Obsidian
					player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, potionDuration*2,2));
					player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, potionDuration*2,2));
					break;					
				case PUMPKIN: // Pumpkin
					ItemStack helmet = player.getInventory().getHelmet();
					if (helmet == null) {
						helmet = new ItemStack(Material.AIR, 1, (short) 14);
					}
					if (helmet.getType()!= Material.PUMPKIN) {
						currentPlayerObject.setHelmetWorn(helmet);
						player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

						player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN, 1, (short) 14));
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(InsanityRun.plugin, new BukkitRunnable() {
						public void run() {
							player.getInventory().setHelmet(currentPlayerObject.getHelmetWorn());
						}
					}, 20 * 2); // 20 ticks per second x 'gracetime' seconds
					break;
				case ICE: // ICE = freeze player
					currentPlayerObject.setFrozen(true);
					Bukkit.getScheduler().scheduleSyncDelayedTask(InsanityRun.plugin, new BukkitRunnable() {
						public void run() {
							currentPlayerObject.setFrozen(false);
						}
					}, 20 * 2); // 20 ticks per second x 'gracetime' seconds			
					break;
				case SPONGE: // Sponge
					player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 1);
					player.setVelocity(player.getLocation().getDirection().multiply(-1));
					break;
				case GLOWSTONE: // Glowstone - checkpoint
					currentPlayerObject.setLastCheckpoint(player.getLocation());
					player.getWorld().playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
					break;
				case REDSTONE_BLOCK: // Redstone - finish line
					if (currentPlayerObject.getInGame()) {
						endLevelOrGame(currentPlayerObject);
					}
					break;
				case WATER: // water
				case STATIONARY_WATER: // stationary water
				case LAVA: // lava
				case STATIONARY_LAVA: // stationary lava
					if (currentPlayerObject.getInGame()) {
						currentPlayerObject.setInGame(false);
						if (blockOn == Material.WATER || blockOn == Material.STATIONARY_WATER) player.getWorld().playSound(player.getLocation(), Sound.SPLASH, 1, 1);
						if (blockOn == Material.LAVA || blockOn == Material.STATIONARY_LAVA) player.getWorld().playSound(player.getLocation(), Sound.LAVA, 1, 1);
						Bukkit.getScheduler().scheduleSyncDelayedTask(InsanityRun.plugin, new BukkitRunnable() {
							public void run() {
								if (InsanityRun.plugin.getConfig().getBoolean("waterRestartsRun")) {
									waterRestart(currentPlayerObject);
								}
								else if (InsanityRun.plugin.getConfig().getBoolean("useCheckpoints")) {
									checkpointRestart(currentPlayerObject);
								}
								else {
									defaultRestart(currentPlayerObject);
								}
							}
						}, 20 * 1); // 20 ticks per second x 1 seconds
					}
					break;
				default:
					break;
				}
				InsanityRun.playerObject.put(playerName,currentPlayerObject);
			}
		}
	}

	// Stop player going hungry
	@EventHandler(priority = EventPriority.MONITOR)
	public static void onFoodLevelChangeEvent(FoodLevelChangeEvent event){
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			final String playerName = player.getName();
			iPlayer playerObject = InsanityRun.playerObject.get(playerName);
			if (playerObject != null) {
				if( playerObject.getInGame()) { 
					event.setCancelled(true);
				}
			}
		}
	}

	// Notice when they leave and remove them from the game data
	@EventHandler
	public void onLeave(PlayerQuitEvent event){
		Player player = event.getPlayer();
		String playerName = player.getName();
		iPlayer playerObject = InsanityRun.playerObject.get(playerName);
		if (playerObject != null) {
			Location loc = playerObject.getSignClickLoc();
			InsanityRun.playerQuitList.put(playerName,loc);
			GameManager.refundMoney(playerObject.getInArena(),playerName);
			GameManager.gameOver2(player, playerObject.getInArena());
		}
	}

	// Notice when they come back again and move to clicksign location
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ArrayList<String> playersToRemove = new ArrayList<String>();
		Player joinPlayer = event.getPlayer();
		Iterator<String> iterator = InsanityRun.playerQuitList.keySet().iterator();
		while(iterator.hasNext()) {
			String playerName = iterator.next();
			if (joinPlayer.getName().equals(playerName)) {
				Location loc = InsanityRun.playerQuitList.get(playerName);
				joinPlayer.teleport(loc);
				playersToRemove.add(playerName);
				//InsanityRun.playerQuitList.remove(playerName);
				joinPlayer.setFireTicks(0);
				for (PotionEffect effect : joinPlayer.getActivePotionEffects()) {
					joinPlayer.removePotionEffect(effect.getType());
				}
				joinPlayer.getInventory().setHelmet(new ItemStack(Material.AIR, 1, (short) 14));
			}
		}
		// Remove players from playerQuitList - fixes ConcurrentModification bug
		for (String delPlayers:playersToRemove) {
			InsanityRun.playerQuitList.remove(delPlayers);
		}
	}

	// Stop all player damage, including fire from lava
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			final String playerName = player.getName();
			iPlayer playerObject = InsanityRun.playerObject.get(playerName);
			if (playerObject != null) {
				event.setCancelled(true);
				event.getEntity().setFireTicks(0);
			}
		}
	}

	// If landing in water or lava restarts the run
	private static void waterRestart(iPlayer currentPlayerObject) {
		Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
		currentPlayerObject.setCoins(0);
		currentPlayerObject.setFrozen(false);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		currentPlayerObject.setInGame(true);
		currentPlayerObject.clearGoldWalkedArray();
		GameManager.teleportToSpawn(player, currentPlayerObject.getInArena());
		GameManager.updatePlayerXYZ(player);
	}

	// Checkpoint restart
	private static void checkpointRestart(iPlayer currentPlayerObject) {
		Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
		player.setFireTicks(0);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		currentPlayerObject.setInGame(true);
		if (currentPlayerObject.getLastCheckpoint()!=null) {
			player.teleport(currentPlayerObject.getLastCheckpoint());
			GameManager.updatePlayerXYZ(player);
		}
		else {
			GameManager.teleportToSpawn(player, currentPlayerObject.getInArena());
			GameManager.updatePlayerXYZ(player);
		}
	}

	// Default restart
	private static void defaultRestart(iPlayer currentPlayerObject) {
		Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
		player.setFireTicks(0);
		player.sendMessage(ChatColor.GOLD + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".gameOver") + " " + currentPlayerObject.getCoins() + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".gameCurrency"));
		player.teleport(currentPlayerObject.getSignClickLoc());
		GameManager.gameOver(player, currentPlayerObject.getInArena(), currentPlayerObject);
		GameManager.updatePlayerXYZ(player);
	}

	// Player ran over Redstone. End the level and start next or end game
	private static void endLevelOrGame(final iPlayer currentPlayerObject) {
		Long runTime = System.currentTimeMillis()-currentPlayerObject.getStartRaceTime();
		final Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
		Location loc = player.getPlayer().getLocation();
		String playerName = currentPlayerObject.getPlayerName();
		final String arenaName = currentPlayerObject.getInArena();
		currentPlayerObject.setInGame(false);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		Random r = new Random();   
		Type type = Type.BALL;       
		Color c1 = Color.GREEN;
		Color c2 = Color.YELLOW;
		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
		fwm.addEffect(effect);
		fwm.setPower(1);
		fw.setFireworkMeta(fwm);
		if (InsanityRun.broadcastWins) {
			InsanityRun.plugin.getServer().broadcastMessage(ChatColor.GOLD + "[Insanity Run]" + String.format(InsanityRun.broadcastWinsText, colourise("&3"+playerName+"&6"), colourise("&9"+arenaName+"&6"), colourise("&a"+currentPlayerObject.getCoins()+"&6"), InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".gameCurrency"), colourise("&f"+formatIntoHHMMSS(runTime))));
		}
		else {
			player.sendMessage(ChatColor.GOLD + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".gameOver") + " " + colourise("&a"+currentPlayerObject.getCoins()+"&6") + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".gameCurrency") + " Time: " + colourise("&f"+formatIntoHHMMSS(runTime)));
		}
		// Update Score sign, if any
		scoresUpdate(arenaName,playerName,runTime,currentPlayerObject.getCoins());
		
		if (InsanityRun.useVault && InsanityRun.plugin.getConfig().getInt(arenaName + ".pay") > 0) {
			EconomyResponse res = InsanityRun.economy.depositPlayer(player.getName(), InsanityRun.plugin.getConfig().getInt(arenaName + ".pay"));
			if(res.transactionSuccess()) {
				player.sendMessage(ChatColor.GOLD + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".vaultAward") + " " + InsanityRun.plugin.getConfig().getInt(arenaName + ".pay") + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".payCurrency"));
			} else {
				player.sendMessage(String.format("An error occured: %s", res.errorMessage));
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(InsanityRun.plugin, new BukkitRunnable() {
			public void run() {
				// Check for linked arena
				if (InsanityRun.plugin.getConfig().getString(arenaName + ".link")!=null) {
					if ((!InsanityRun.useVault) || (InsanityRun.useVault && canAfford(currentPlayerObject,InsanityRun.plugin.getConfig().getString(arenaName + ".link")))) {
						// Remove player from this arena
						int playCount;
						playCount = InsanityRun.playersInThisArena.get(arenaName);
						playCount--;
						InsanityRun.playersInThisArena.put(arenaName, playCount);
						GameManager.updateJoinSign(arenaName);
						currentPlayerObject.setCoins(0);
						GameManager.teleportToSpawn(player, InsanityRun.plugin.getConfig().getString(arenaName + ".link"));
						GameManager.updatePlayerXYZ(player);
						currentPlayerObject.setLastCheckpoint(player.getLocation());
						currentPlayerObject.setInArena(InsanityRun.plugin.getConfig().getString(arenaName + ".link"));
						currentPlayerObject.setFrozen(false);
						currentPlayerObject.setLastMovedTime(System.currentTimeMillis());
						currentPlayerObject.setStartRaceTime(System.currentTimeMillis());
						currentPlayerObject.setInGame(true);
						if (InsanityRun.playersInThisArena.get(InsanityRun.plugin.getConfig().getString(arenaName + ".link")) == null) {
							InsanityRun.playersInThisArena.put(InsanityRun.plugin.getConfig().getString(arenaName + ".link"), 0);
						}
						playCount = InsanityRun.playersInThisArena.get(InsanityRun.plugin.getConfig().getString(arenaName + ".link"));
						playCount++;
						InsanityRun.playersInThisArena.put(InsanityRun.plugin.getConfig().getString(arenaName + ".link"), playCount);
						GameManager.updateJoinSign(InsanityRun.plugin.getConfig().getString(arenaName + ".link"));
						return;
					}
				}
				// Check if player gets sent back to JOIN sign
				if (!InsanityRun.plugin.getConfig().getBoolean("noEndTeleport")) {
					GameManager.updateJoinSign(arenaName);
					player.teleport(currentPlayerObject.getSignClickLoc());
					GameManager.updatePlayerXYZ(player);
				}
				// Run GameOver stuff
				GameManager.gameOver(player, arenaName, currentPlayerObject);
			}
		}, 20 * 5); // 20 ticks per second x 5 seconds
	}

	// Can player afford to play next arena?
	private static boolean canAfford(iPlayer currentPlayerObject, String arenaName) {
		Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
		String playerName = currentPlayerObject.getPlayerName();
		// Does player have enough money to play?
		if (InsanityRun.useVault) {
			if (InsanityRun.economy.getBalance(player.getName()) < InsanityRun.plugin.getConfig().getInt(arenaName + ".charge")) {
				player.sendMessage(ChatColor.RED + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".notEnoughMoneyText") + InsanityRun.plugin.getConfig().getInt(arenaName + ".charge") + " " + InsanityRun.plugin.getConfig().getString(InsanityRun.useLanguage + ".payCurrency"));
				return false;
			}
			else {
				// Withdraw money
				EconomyResponse r = InsanityRun.economy.withdrawPlayer(playerName, InsanityRun.plugin.getConfig().getInt(arenaName + ".charge"));
				if(r.transactionSuccess()) {
					return true;
				} else {
					player.sendMessage(String.format("An error occured: %s", r.errorMessage));
					return false;
				}
			}
		}
		return false;
	}

	// Prevent removal of pumpkin head
	@EventHandler
	public void onInventoryClick (InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		final String playerName = player.getName();
		iPlayer playerObject = InsanityRun.playerObject.get(playerName);
		if (playerObject != null) {
			if (event.getSlotType() == SlotType.ARMOR) {
				event.setCancelled(true);
			}
		}

	}
	
	// Convert time
	private static String formatIntoHHMMSS(Long millisecs)
	{
		int secs = (int) (millisecs / 1000);
		int remainder = secs % 3600;
		int minutes = remainder / 60;
		int seconds = remainder % 60;

		return new StringBuilder().append(minutes).append(":").append(seconds < 10 ? "0" : "").append(seconds).toString();
	}
	// Colourise strings
	public static String colourise(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	// Update score times
	private static void scoresUpdate(String arenaName, String playerName, Long runTime, int coins) {
		World world;
		String worldtemp;
		if (InsanityRun.plugin.getConfig().getString(arenaName + ".fastsign.world")==null) { return; }
		worldtemp = InsanityRun.plugin.getConfig().getString(arenaName + ".fastsign.world");
		world = InsanityRun.plugin.getServer().getWorld(worldtemp);
		double x = InsanityRun.plugin.getConfig().getDouble(arenaName + ".fastsign.x");
		double y = InsanityRun.plugin.getConfig().getDouble(arenaName + ".fastsign.y");
		double z = InsanityRun.plugin.getConfig().getDouble(arenaName + ".fastsign.z");
		Long[] runTimes = new Long[6];
		String[] runTimeNames = new String[6];
		int[] coinsCollected = new int[6];
		// If highest speed has been beaten
		if (runTime < InsanityRun.plugin.getConfig().getLong(arenaName + ".fastest."+1+".time")) {
			if (InsanityRun.plugin.getConfig().getString(arenaName + ".fastest.1.name") == null) return;
			signUpdate(new Location(world, x , y, z),playerName, runTime, arenaName);
		}
		
		// Get current fastest records
		for (int i = 1; i < 6; i++) {
			runTimes[i] = InsanityRun.plugin.getConfig().getLong(arenaName + ".fastest."+i+".time");
			runTimeNames[i] = InsanityRun.plugin.getConfig().getString(arenaName + ".fastest."+i+".name");
			coinsCollected[i] = InsanityRun.plugin.getConfig().getInt(arenaName + ".fastest."+i+".coins");
		}
		// See if current record is faster
		for (int i = 1; i < 6; i++) {
			if (runTime < runTimes[i]) {
				for (int j = 4; j > i-1; j--) {
					runTimes[j+1]=runTimes[j];
					runTimeNames[j+1]=runTimeNames[j];
					coinsCollected[j+1]=coinsCollected[j];
				}
				runTimes[i]=runTime;
				runTimeNames[i]=playerName;
				coinsCollected[i]=coins;
				break;
			}
		}
		
		// Update plugin data
		for (int i = 5; i > 0; i--) {
			InsanityRun.plugin.getConfig().set(arenaName + ".fastest."+i+".name", runTimeNames[i]);
			InsanityRun.plugin.getConfig().set(arenaName + ".fastest."+i+".time", runTimes[i]);
			InsanityRun.plugin.getConfig().set(arenaName + ".fastest."+i+".coins", coinsCollected[i]);
		}
		InsanityRun.plugin.saveConfig();
		bottomSignUpdate(new Location(world, x , y, z), arenaName);
	}
	
	// Actual sign updating code
	public static void signUpdate(Location bLocation, String playerName, Long runTime, String arenaName) {
		World w = bLocation.getWorld();
		Material bm = w.getBlockAt(bLocation).getType();
		Block b = w.getBlockAt(bLocation);
		if(bm == Material.SIGN_POST || bm == Material.WALL_SIGN) {
			Sign sign = (Sign) b.getState();
			if (playerName.length()> 9) playerName = playerName.substring(0,9);
			sign.setLine(3, playerName + " " + formatIntoHHMMSS(runTime));
			sign.update(); // Update sign
		}
	}
	// Update bottom sign
	public static void bottomSignUpdate(Location bLocation, String arenaName) {
		bLocation.setY(bLocation.getY()-1);
		World w = bLocation.getWorld();
		Block b = w.getBlockAt(bLocation);
		Material bm = w.getBlockAt(bLocation).getType();
		if(bm == Material.SIGN_POST || bm == Material.WALL_SIGN) {
			String signName;
			Long signTime;
			Sign sign = (Sign) b.getState();
			for (int i = 2; i<6; i++) {
				signName = InsanityRun.plugin.getConfig().getString(arenaName + ".fastest."+i+".name");
				signTime = InsanityRun.plugin.getConfig().getLong(arenaName + ".fastest."+i+".time");
				if (signName.length() > 9) signName = signName.substring(0, 9);
				sign.setLine(i-2, signName + " " + formatIntoHHMMSS(signTime));
			}
			sign.update();
		}
	}
}
