package blockdata;

import java.util.HashMap;

public class TileInfo {
	public String tileName;
	public boolean solid;
	public char inputChar;
	public int height;
	
	public static final HashMap<Integer, Boolean> inTileMap;
	static {
		inTileMap = new HashMap<Integer, Boolean>();
		inTileMap.put(14, true);
		inTileMap.put(15, true);
		
		
		
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
			new TileInfo("???", true, 'a'),
			new TileInfo("???", true, 'b'),
			new TileInfo("???", true, 'c'),
			new TileInfo("???", true, 'd'),
			new TileInfo("ButtonOn", true, 'e', 5),
			new TileInfo("ButtonOff", true, 'f', 5),
			new TileInfo("Grass", true, 'g'),
			new TileInfo("Portal", false, 'p')
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
