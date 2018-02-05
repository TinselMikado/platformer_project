package blockdata;

import java.util.HashMap;

public class LevelCreate {
	
	private Tile[] level;
	private HashMap<InteractiveTile, Integer> iTMap; //stores an interactive tile and its trigger id
	private int playerSpawnTile; // returns TILE NUMBER of player spawn
	
	public LevelCreate(String levelStr, String itStr) {
		level = new Tile[levelStr.length()];
		iTMap = new HashMap<InteractiveTile, Integer>();
		playerSpawnTile = levelStr.indexOf('?');
		int itStrchar = 0;
		
		
		for(int i = 0; i<levelStr.length(); i++) {
			if(!TileInfo.inTileMap.containsKey(findIDfromChar(levelStr.charAt(i)))) {
				Tile t = new Tile((i%40)*32, (i/40)*32, findIDfromChar(levelStr.charAt(i)));
				level[i] = t; //creates array of tiles using the string of id numbers from LevelLoader
			}
			else {
				InteractiveTile t = new InteractiveTile((i%40)*32, (i/40)*32, findIDfromChar(levelStr.charAt(i)), Character.getNumericValue(itStr.charAt(itStrchar)), false);
				itStrchar++;
				t.setSwitch(TileInfo.inTileMap.get(t.getID()));
				level[i] = t;
				iTMap.put(t, t.getTrID());
			}
		}
		System.out.println(itStrchar);
	}
	
	public Tile[] getArray() {
		return level;
	}
	
	public int getPS() {
		return playerSpawnTile;
	}
	
	public HashMap<InteractiveTile, Integer> getInTileMap(){
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