package main;

import java.awt.Graphics;

import blockdata.Tile;

public class LevelCreate {
	
	private SpriteSheet ss;
	private Tile[] level;
	
	public LevelCreate(String levelStr, SpriteSheet sprsht) {
		level = new Tile[levelStr.length()];
		ss = sprsht;
		for(int i = 0; i<levelStr.length(); i++) {
			level[i] = new Tile((i%40)*32, (i/40)*32, Character.getNumericValue(levelStr.charAt(i))); //creates array of tiles using the string of id numbers from LevelLoader
		}
	}
	
	public Tile[] getArray() {
		return level;
	}
	
	public void render(Graphics g) {
		for(int i = 0; i < level.length; i++) {
			Tile t = level[i];
			g.drawImage(ss.getSprite(t.getID()), t.getX(), t.getY(), 32, 32, null);
		}
	}
}

//probably couldve done LevelLoader and LevelCreate in one class but this is more readable