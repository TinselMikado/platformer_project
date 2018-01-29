package main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BackgroundRendering {
	
	private BufferedImage backgroundImage;
	
	public BackgroundRendering() {
		
		try {
			backgroundImage = ImageIO.read(getClass().getClassLoader().getResource("resources/bg.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void render(Graphics g, int x, int y) {
		g.drawImage(backgroundImage, -x/2, -y/2, backgroundImage.getWidth(), backgroundImage.getHeight(), null);
	}

}
