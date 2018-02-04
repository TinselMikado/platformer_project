package blockdata;

import java.awt.Graphics;
import java.util.HashMap;

import main.SpriteSheet;
import main.Tuple;

public class LevelManager {
	private static Tile[] level;
	private HashMap<InteractiveTile, Byte> inTileMap;
	private SpriteSheet textureSheet;
	private String levelPath;
	private TileShadow ts;
	private int playerSpawn;
	private int levelNumber;
	
	public LevelManager(SpriteSheet ss) {		
		textureSheet = ss;		
	}
	
	public void initializeLevel(int levelNumber) {
		this.levelNumber = levelNumber;
		levelPath = "/resources/level" + Integer.toString(levelNumber) + ".txt";
		loadLevel();
		ts = new TileShadow(level);
	}
	
	public Tile[] getArray() {
		return level;
	}
	
	public int getLevelNumber() {
		return levelNumber;
	}
	
	public int getSpawnTile() {
		return playerSpawn;
	}
	
	public void loadLevel() {
		LevelLoader ll = new LevelLoader(levelPath);
		createLevel(ll.getLevelString());
	}
	
	public void createLevel(String ls) {
		LevelCreate lc = new LevelCreate(ls);
		level = lc.getArray();
		inTileMap = lc.getInTileMap();
		playerSpawn = lc.getPS();
	}
	
	public HashMap<InteractiveTile, Byte> getInTileMap(){
		return inTileMap;
	}
	
	public void render(Graphics g) {
		for(int i = 0; i < level.length; i++) {
			Tile t = level[i];
			g.drawImage(textureSheet.getSprite(t.getSpriteNumber()), t.getX(), t.getY(), 32, 32, null);			
		}
		
		ts.render(g);
	}
	
	public static int b2i(boolean b) {
		return b?1:0;
	}


}
