package main;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.File;

import audio.AudioHandler;
import blockdata.LevelCreate;
import blockdata.LevelLoader;
import blockdata.LevelManager;
import input.KbInput;
import input.MouseInput;

@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable{
	
	private boolean running = false;
	private Thread thread;
	public static final int FWIDTH = 640;
	public static final int FHEIGHT = 480;
	public static final String TITLE = "Game";
	public static final int SCALE = 2;
	public static final int PIXEL_AMOUNT = FWIDTH * FHEIGHT * SCALE * SCALE;
	public static final Dimension gd = new Dimension(FWIDTH*SCALE, FHEIGHT*SCALE);
	public Player player;
	public LevelLoader lvlL;
	public LevelCreate lvlC;
	public LevelManager lm;
	public SpriteSheet textures;
	public SpriteSheet characterSprites;
	public AudioHandler audioHandler;
	public BackgroundRendering bgR;
	
	
	public Game() {
		File[] folder = new File("src/resources").listFiles();
		for(File f : folder) {
			//System.out.println(f);
		}
		textures = new SpriteSheet("resources/spritesheet.png", 16);	 	//loads all textures
		characterSprites = new SpriteSheet("resources/charactersprites.png", 16); //loads character sprites
		
		addKeyListener(new KbInput(this));					//loads keyboard input
		addMouseListener(new MouseInput(this));				//loads mouse input
		audioHandler = new AudioHandler();					//loads audio
		audioHandler.playMusic();							//plays bgmusic
		lm = new LevelManager(textures); //loads and creates level from textfile
		lm.initializeLevel(1);
		player = new Player(characterSprites, lm, audioHandler);	//creates player
		bgR = new BackgroundRendering();
		
	}
	
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 120;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				tick();
				delta--;
			}
			if(running)
				render();
				frames++;
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				//System.out.println("FPS: " + frames);;
				frames = 0;
			}
		}
		stop();
	}
	
		
	private void tick() {
		player.tick();
	}
	
	private void render() {
		
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		//draw background
		g.setColor(Color.black);
		g.fillRect(0, 0, FWIDTH*SCALE, FHEIGHT*SCALE);
		bgR.render(g, player.getX(), player.getY());
		lm.render(g); //render the level & tileshadow
		player.render(g); //render the player
		g.dispose();
		bs.show();
	}
	
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		switch(k) {
		
		case 37: //LEFT ARROW MOVE LEFT
			player.setDir(0, 1);
			break;
		case 90: //Z - jump
			player.setDir(1, 1);
			break;
		case 39: //RIGHT ARROW
			player.setDir(2, 1);
			break;
		case 40: //DOWN ARROW
			player.setDir(3, 1);
			break;
		case 32:
			if(!player.getGhost().isDeployed())
				player.deployGhost();
			break;
		case 82:
			player.resetGhost();
			break;
		case 33:
			audioHandler.nextMusic();
			break;
		case 34:
			audioHandler.prevMusic();
			break;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();
		switch(k) {
		
		case 37: //LEFT ARROW
			player.setDir(0, 0);
			break;
		case 90: //Z
			player.setDir(1, 0);
			break;
		case 39: //RIGHT ARROW
			player.setDir(2, 0);
			break;
		case 40: //DOWN ARROW
			player.setDir(3, 0);
			break;
		}
	}
	
	public void mousePressed(MouseEvent e) {
		player.setX(e.getX());
		player.setY(e.getY());
	}
	
	public void printPlayerInfo() {
		System.out.println("Player x = " + player.getX());
		System.out.println("Player y = " + player.getY());
		System.out.println("Player xVel = " + player.getXV());
		System.out.println("Player yVel = " + player.getYV());
		System.out.println("player y +yV + 64 = " + (player.getY() + player.getYV() + 64));
		System.out.println("tilesetnumber for tbelow = " + player.xyCoordToTileSet(player.getX(), (int)(player.getY() + player.getYV() + 64)));
		System.out.println("tilebelow currently at: " + player.tBelow.getY());

	}
	
	
	public static void main(String[] args){
		Game game = new Game();
		new Window(FWIDTH*SCALE, FHEIGHT*SCALE, TITLE, game);
		game.start();
	}
}

