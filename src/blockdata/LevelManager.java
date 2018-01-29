package blockdata;

import java.awt.Graphics;
import java.util.HashMap;

import main.SpriteSheet;

public class LevelManager {
	private static Tile[] level;
	private HashMap<InteractiveTile, Byte> inTileMap;
	private SpriteSheet textureSheet;
	private String levelPath;
	private TileShadow ts;
	
	public LevelManager(SpriteSheet ss, String levelPath) {
		
		textureSheet = ss;
		this.levelPath = levelPath;
		loadLevel();
		ts = new TileShadow(level);
		
	}
	
	public Tile[] getArray() {
		return level;
	}
	
	public void loadLevel() {
		LevelLoader ll = new LevelLoader(levelPath);
		createLevel(ll.getLevelString(), textureSheet);
	}
	
	public void createLevel(String ls, SpriteSheet ts) {
		LevelCreate lc = new LevelCreate(ls, ts);
		level = lc.getArray();
		inTileMap = lc.getInTileMap();
	}
	
	public HashMap<InteractiveTile, Byte> getInTileMap(){
		return inTileMap;
	}
	
	public void render(Graphics g) {
		for(int i = 0; i < level.length; i++) {
			Tile t = level[i];
			g.drawImage(textureSheet.getSprite(t.getID()), t.getX(), t.getY(), 32, 32, null);			
		}
		
		ts.render(g);
	}
	
	public static int b2i(boolean b) {
		return b?1:0;
	}

	public static void update(InteractiveTile it, boolean state, int pos) {
		level[pos] = new InteractiveTile(it.x, it.y, it.tileID-b2i(state), it.getTrID(), it.getNatState());
	}
}
