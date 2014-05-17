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
import java.awt.Point;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;


public class iPlayer {
	private String playerName;
	private String inArena;
	private int coinsCollected;
	private int lastLocX;
	private int lastLocY;
	private int lastLocZ;
	private String arenaWorld;
	private int idleLocX;
	private int idleLocZ;
	private Boolean inGame;
	private Boolean isFrozen;
	private Long lastMovedTime;
	private Long startRaceTime;
	private ItemStack helmetWorn;
	private Location signClickLoc;
	private ArrayList<Point> goldWalked = new ArrayList<>();
	private Location lastCheckpoint;
	private int idleCount;
	
	// Getters and Setters
	public int getIdleCount() {
		return idleCount;
	}
	public void setIdleCount(int idleCount){
		this.idleCount=idleCount;
	}	
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName){
		this.playerName=playerName;
	}
	public Location getLastCheckpoint() {
		return lastCheckpoint;
	}
	public void setLastCheckpoint(Location loc){
		this.lastCheckpoint=loc;
	}
	public String getInArena() {
		return inArena;
	}
	public void setInArena(String Arena){
		this.inArena=Arena;
	}	
	public int getCoins() {
		return coinsCollected;
	}
	public void setCoins(int coins){
		coinsCollected = coins;
	}
	public int getLastX() {
		return lastLocX;
	}
	public void setLastX(int lastX) {
		this.lastLocX=lastX;
	}
	public int getLastY() {
		return lastLocY;
	}
	public void setLastY(int lastY) {
		this.lastLocY=lastY;
	}
	public int getLastZ() {
		return lastLocZ;
	}
	public void setLastZ(int lastZ) {
		this.lastLocZ=lastZ;
	}	

	public String getArenaWorld() {
		return arenaWorld;
	}
	public void setArenaWorld(String world) {
		this.arenaWorld=world;
	}
	
	public int getIdleX() {
		return idleLocX;
	}
	public void setIdleX(int idleX) {
		this.idleLocX=idleX;
	}	
	public int getIdleZ() {
		return idleLocZ;
	}
	public void setIdleZ(int idleZ) {
		this.idleLocZ=idleZ;
	}		
	public Boolean getInGame() {
		return inGame;
	}
	public void setInGame(boolean inGame) {
		this.inGame=inGame;
	}
	public Boolean getFrozen() {
		return isFrozen;
	}
	public void setFrozen(boolean isFrozen){
		this.isFrozen=isFrozen;
	}
	public Long getLastMovedTime(){
		return lastMovedTime;
	}
	public void setLastMovedTime(Long lastMovedTime){
		this.lastMovedTime=lastMovedTime;
	}
	public Long getStartRaceTime(){
		return startRaceTime;
	}
	public void setStartRaceTime(Long startRaceTime){
		this.startRaceTime=startRaceTime;
	}
	public ItemStack getHelmetWorn(){
		return helmetWorn;
	}
	public void setHelmetWorn(ItemStack helmetWorn) {
		this.helmetWorn=helmetWorn;
	}
	
	public Location getSignClickLoc(){
		return signClickLoc;
	}
	public void setSignClickLoc(Location signClickLoc) {
		this.signClickLoc=signClickLoc;
	}
	
	public ArrayList<Point> getGoldWalkedArray() {
		return goldWalked;
	}

	public void setGoldWalkedArray(ArrayList<Point> arrList) {
		this.goldWalked = arrList;
	}
	public void clearGoldWalkedArray() {
		this.goldWalked.clear();
	}
}
