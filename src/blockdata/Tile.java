package blockdata;

import java.awt.Rectangle;

public class Tile {

	protected int x, y;
	protected int tileID;
	protected boolean solid;
	protected Rectangle r;
	
	private static final boolean[] tileMap = {false, true, true, true, true, true, true, true, true, true}; //air, dirt, player1,2,3, grass, portal?
	
	public Tile(int x, int y, int tileID) {
		this.x=x;
		this.y=y;
		this.tileID = tileID;
		solid = tileMap[this.tileID];
		r= new Rectangle(x, y, 32, 32);
	}
	
	public boolean isSolid() {
		return solid;
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
