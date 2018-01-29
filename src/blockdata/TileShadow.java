package blockdata;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;

public class TileShadow {
	
	private BufferedImage shadowImage;
	private int width = Game.FWIDTH;
	private int height = Game.FHEIGHT;
	private int scale = Game.SCALE;
	private int[] tileShadowValues;
	
	public TileShadow(Tile[] level) {
		tileShadowValues = createAlphaMap(level);
		shadowImage = createImage(tileShadowValues);
	}
	
	public BufferedImage createImage(int[] alphaValues) {
		
		
		BufferedImage image = new BufferedImage(4*alphaValues.length/120, 3*alphaValues.length/120, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, 4*alphaValues.length/120, 3*alphaValues.length/120, alphaValues, 0, 4*alphaValues.length/120);
		
		return image;
		
	}
	
	public int[] createAlphaMap(Tile[] levelArray) {
		
		int[] p = new int[levelArray.length];
		
		for(int i = 0; i < p.length; i++) {
			if(levelArray[i].getID()!=0)
				p[i] = calculateAlphaValue(levelArray, levelArray[i]);
			else
				p[i] = 0;
		}
		
		return p;
	}
	
	public int calculateAlphaValue(Tile[] levelArr, Tile t) {
		
		int distance = findClosestOpenTileDistance(levelArr, t, 255);
		return 0x00000000|(distance<<24);
		
	}
	
	public int findClosestOpenTileDistance(Tile[] levelArr, Tile t, int max) {
		
		int closeDis = max;
		Tile cTile;
		
		for(int i = 0; i < levelArr.length; i++) { //loop
			
			cTile = levelArr[i];
			if(cTile.getID()==0) {
				closeDis = Math.min(distanceCalc(cTile.getX(), cTile.getY(), t.getX(), t.getY()), closeDis);
				if(closeDis<=32)
					break;
			}
			
		}
		closeDis -= 32;
		return closeDis;
	}
	
	public int distanceCalc(int x1, int y1, int x2, int y2) {
		return (int)Math.sqrt(Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2));
	}
	
	public void render(Graphics g) {
		g.drawImage(shadowImage, 0, 0, width*scale, height*scale, null);
	}
	
	public BufferedImage getShadowImage() {
		return shadowImage;
	}
}
