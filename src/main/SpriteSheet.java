package main;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	private BufferedImage[] spritesheet;
	public int width;
	public int height;
	private BufferedImage image;
	
	public SpriteSheet(String path, int size) {
		
		if(path==null)
			return;
		
		try {
			image = ImageIO.read(getClass().getClassLoader().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(image==null)
			return;
		
		width = image.getWidth();
		height = image.getHeight();
		int xtiles = width / size;
		int ytiles = height / size;
		
		spritesheet = new BufferedImage[xtiles*ytiles];
		
		for(int i = 0; i < spritesheet.length; i++) {
			spritesheet[i] = image.getSubimage( (i%xtiles)*size, (i/xtiles)*size, size, size);			
		}
	}
	
	public BufferedImage getSprite(int i) {
		return spritesheet[i];
	}
}
