package blockdata;

import java.util.HashMap;

public class LevelCreate {
	
	private Tile[] level;
	private HashMap<InteractiveTile, Byte> iTMap; //stores an interactive tile and its trigger id
	private byte trgID;
	private int playerSpawnTile; // returns TILE NUMBER of player spawn
	
	public LevelCreate(String levelStr) {
		level = new Tile[levelStr.length()];
		trgID = 0;
		iTMap = new HashMap<InteractiveTile, Byte>();
		playerSpawnTile = levelStr.indexOf('?');
		
		
		for(int i = 0; i<levelStr.length(); i++) {
			if(!TileInfo.inTileMap.containsKey(findIDfromChar(levelStr.charAt(i)))) {
				Tile t = new Tile((i%40)*32, (i/40)*32, findIDfromChar(levelStr.charAt(i)));
				level[i] = t; //creates array of tiles using the string of id numbers from LevelLoader
			}
			else {
				InteractiveTile t = new InteractiveTile((i%40)*32, (i/40)*32, findIDfromChar(levelStr.charAt(i)), trgID, false);
				t.setSwitch(TileInfo.inTileMap.get(t.getID()));
				level[i] = t;
				//trgID++;
				iTMap.put(t, t.getTrID());
			}
		}
	}
	
	public Tile[] getArray() {
		return level;
	}
	
	public int getPS() {
		return playerSpawnTile;
	}
	
	public HashMap<InteractiveTile, Byte> getInTileMap(){
		return iTMap;
	}
	
	public int findIDfromChar(char c) {
		for(int i = 0; i<TileInfo.tileMap.length; i++) {
			if(TileInfo.tileMap[i].inputChar==c)
				return i;
		}
		
		return 0;		
	}
}

//probably couldve done LevelLoader and LevelCreate in one class but this is more readable