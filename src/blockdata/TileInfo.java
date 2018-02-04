package blockdata;

import java.util.HashMap;

public class TileInfo {
	public String tileName;
	public boolean solid;
	public char inputChar;
	public int height;
	
	public static final HashMap<Integer, Boolean> inTileMap;
	static {
		inTileMap = new HashMap<Integer, Boolean>(); // integer = tile id, boolean = Switch or not
		inTileMap.put(15, true); //button
		inTileMap.put(17, false); // gate
		
		
		
	}
	
	public static final TileInfo[] tileMap = 
			
		   {new TileInfo("Air", false, '0'),
			new TileInfo("Stone", true, '1'),
			new TileInfo("StoneTLCorner", true, '2'),
			new TileInfo("StoneTRCorner", true, '3'),
			new TileInfo("StoneBRCorner", true, '4'),
			new TileInfo("StoneBLCorner", true, '5'),
			new TileInfo("StoneTEdge", true, '6'),
			new TileInfo("StoneBEdge", true, '7'),
			new TileInfo("StoneREdge", true, '8'),
			new TileInfo("StoneLEdge", true, '9'),
			new TileInfo("PlayerSpawn", false, '?'), //spawn poiint
			new TileInfo("Exit", false, '+'), // level exit tiles
			new TileInfo("???", true, 'c'),
			new TileInfo("???", true, 'd'),
			new TileInfo("???", true, 'e'),
			new TileInfo("Button", true, 'f', 8), //button (default off)
			new TileInfo("???", true, 'f'),
			new TileInfo("Gate", true, 'G')	// gates (default closed)
			};
	
	public TileInfo(String s, boolean b, char c, int h) {
		tileName = s;
		solid = b;
		inputChar = c;
		height = h;
	}
	public TileInfo(String s, boolean b, char c) {
		tileName = s;
		solid = b;
		inputChar = c;
		height = 0;
	}
}
