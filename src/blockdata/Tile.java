package blockdata;

import java.awt.Rectangle;

public class Tile {

	protected int x, y;
	protected int tileID;
	protected boolean solid;
	protected Rectangle r;
	protected String name;
	protected char inputChar;
	protected int height;
	protected int spriteNumber;
	
	
	public Tile(int x, int y, int tileID) {
		this.x=x;
		this.y=y;
		this.tileID = tileID;
		height = TileInfo.tileMap[tileID].height;		//physical height of block (reversed) height = 0 is full block, height = 4 is 28 pixels high block, height = 16 half block
		solid = TileInfo.tileMap[tileID].solid;
		inputChar = TileInfo.tileMap[tileID].inputChar;
		name = TileInfo.tileMap[tileID].tileName;
		r = new Rectangle(x, y, 32, 32);
		spriteNumber  = tileID;
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public int getSpriteNumber() {
		return spriteNumber;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getID() {
		return tileID;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	
	public Rectangle getR() {
		return r;
	}
}
