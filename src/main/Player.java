package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import audio.AudioHandler;
import blockdata.Tile;
import ghost.GhostInfo;
import ghost.GhostPlayer;

public class Player {
	
	private int x, y;
	private double xV, yV;
	private int action = 0; // 0 = idle, 1 = running, 2 = jumping, 3 = falling, 4 = slowfall
	private int faceDirection = 1; //-1 left, 1 right
	private int[] keyDir; //stores whether keys are held down, position 0 is VK_LEFT, 1:VK_UP, 2:VK_RIGHT, 3:VK_DOWN, 1 means true, 0 means false
	private boolean falling = false;
	private SpriteSheet ss;
	public int allowJump = 1;
	private int allowDJump = 0;
	private Tile[] level;
	private Tile tAbove, tBelow, tAbove2, tBelow2;
	private int headTile = 8;
	private int prevAct = 0;
	private AudioHandler audioHandler;
	private Random r;
	private GhostPlayer ghost;
	private int currGhostFrame = 0;
	private int jumpType = 0;
	private GhostInfo tempGInfo;
	
	public Player(int x, int y, SpriteSheet ss, Tile[] level, AudioHandler ah) {
		
		this.x = x; this.y = y;
		keyDir = new int[4];
		this.ss=ss;
		this.level = level;
		audioHandler = ah;
		r  = new Random();
		ghost = new GhostPlayer();
		
		tAbove = level[0];
		tBelow = level[0];
		tAbove2 = level[0];
		tBelow2 = level[0];
		
	}
	
	int actionTimer = -1;
	int djumpTimer = 0;
	
	public void tick() {
		
		if(djumpTimer<30)
			djumpTimer++;
		actionTimer++;		
		
		tAbove = level[xyCoordToTileSet(x+2, (int)(y + yV))];
		tBelow = level[xyCoordToTileSet(x+2, (int)(y + yV + 64))];
		tAbove2 = level[xyCoordToTileSet(x+30, (int)(y + yV))];
		tBelow2 = level[xyCoordToTileSet(x+30, (int)(y + yV + 64))];		
		
		xV = checkXBounds(-1)*keyDir[0]*-3 + checkXBounds(1)*keyDir[2]*3; //sets xvelocity to the dir of keys held down, if opposite directions both held down, vel = 0
		
		if(keyDir[0]==1&&keyDir[2]==0)
			faceDirection = -1;
		if(keyDir[2]==1&&keyDir[0]==0)
			faceDirection = 1;
		
		if(!falling&&xV==0)
			action = 0;
		
		if(!falling&&xV!=0)
			action = 1;
			
		
		
		x+=xV; //move player on x by xV
				
		
		checkYBounds(); //checks whether character on the ground or not, sets falling to true/false & 
						//snaps character to ground if falling down and close (to avoid clipping through / weird shit)
		
		if(falling) {
			if(yV<0)
				action = 2; //set action to 'jump' action
			else if(yV>6)
				action = 3; //set action to 'fall' action
			else
				action = 4; //set action to slow fall
			allowJump = 0;
			if(yV<8)
				yV+=0.3;	//apply gravity
			if(allowDJump==0)
				allowDJump = 1 - keyDir[1];
		}
		else{
			yV=0;
			if(allowJump==0)
				allowJump = 1 - keyDir[1]; //only allows player to jump if they are on the ground and have since released the jump button (no holding and perma jumping)
			allowDJump = 0;
			
		}
		
		if(!tAbove.isSolid()&&!tAbove2.isSolid()&&allowJump==1&&keyDir[1]==1) { // if both blocks above are nonsolid, and player pressing jump, and char is allowed to jump, then jump
			jump();
		}
		else if(!tAbove.isSolid()&&!tAbove2.isSolid()&&djumpTimer==30&&allowDJump==1&&keyDir[1]==1) {
			dJump();
		}
		
		y+=yV;
		
		if(prevAct!=action)
			actionTimer = 0; //reset the frame position if playing a different animation (changing action)
		
		headTile = chooseTileFrame(action, actionTimer); //choose the tile depending on what char is doing
		prevAct = action;
		if(actionTimer>600&&action==0)
			action = 0; //change this value later, but idea is if character is idle 10seconds+ then do some 'waiting' animation
		
		
		
		if(!ghost.isDeployed())
			ghost.getInfoList().add(new GhostInfo(new Tuple(x, y), action, faceDirection, actionTimer, jumpType));
		else {
			currGhostFrame = Math.min(currGhostFrame+1, ghost.getInfoList().size()-1);
			tempGInfo = ghost.getInfoList().get(currGhostFrame);
			if(tempGInfo.getJumpType()==1)
				audioHandler.playSFX("jump");
			if(tempGInfo.getJumpType()==2)
				audioHandler.playSFX("dJump");
		}
		
		jumpType = 0;
	}
	
	public void jump() {
		yV = -9; //jump
		audioHandler.playSFX("jump");
		allowJump--;
		djumpTimer = 0;
		jumpType = 1;
	}
	
	public void dJump() {
		yV = -7; //jump
		allowDJump = -1;
		audioHandler.playSFX("dJump");
		jumpType = 2;
	}
	
	public void deployGhost() {
		ghost.setDeployed(true);
		currGhostFrame = 0;
	}
	
	public GhostPlayer getGhost() {
		return ghost;
	}
	
	boolean playedStep = false;
	
	public int chooseTileFrame(int act, int time) { //returns tile of head, body is just +8 (added in render function)
				
		switch (act){
			case 0:
				return 0 + (time/60)%2;
			case 1:
				if((((time/12)%6)-1)%3==0) {
					if(!playedStep) {
						audioHandler.playSFXR("steps", r.nextInt(4));
						playedStep = true;
					}
				}else playedStep = false;
				return 2 + (time/12)%6; //example: 10 is base tile (frame 0), time / {6} because tile changes every {6} ticks, %<6> because there are <6> total tiles
			case 2:
				return 16 + (time/12)%4;
			case 3:
				return 22 + (time/12)%2;
			case 4:
				return 20 + + (time/12)%2;
		}
		return 0;
	}
	
	public int xyCoordToTileSet(int x, int y) {
		return x/32 + (y/32)*40; //gets i value of whatever tile contains this x&y position
	}
	
		
	public int checkXBounds(int dir){
		if(dir==-1) { //check left side
			if (level[xyCoordToTileSet((int)(x+xV-4), (int)(y + yV + 0 ))].isSolid() ||  //checks all 3 possible tiles covering left side for solid tiles
				level[xyCoordToTileSet((int)(x+xV-4), (int)(y + yV + 32))].isSolid() ||
				level[xyCoordToTileSet((int)(x+xV-4), (int)(y + yV + 48))].isSolid())	
				return 0; // if a solid block is found, disallow the movement
		}
		else if(dir==1){ //check right side
			if (level[xyCoordToTileSet((int)(x+xV+36), (int)(y + yV + 0 ))].isSolid() ||
				level[xyCoordToTileSet((int)(x+xV+36), (int)(y + yV + 32))].isSolid()|| //checks all 3 possible tiles covering right side for solid tiles
				level[xyCoordToTileSet((int)(x+xV+36), (int)(y + yV + 48))].isSolid()) {
				return 0; // if a solid block is found, disallow the movement
			}
		}
		
		return 1; //no solid blocks found, allow the movement
	}
	
	public boolean inBounds(int a, int b1, int b2) {
		if(b1<=a && a<=b2)
			return true;
		return false;
	}
	
	

	public void checkYBounds() {		
				
		if(yV>=0) { //IF CHAR IS FALLING DOWNWARDS OR STILL
			if((tBelow.isSolid()||tBelow2.isSolid())) { //AND EITHER BLOCK UNDERNEATH IS SOLID				
				y = level[xyCoordToTileSet(0, (int)(y + yV + 64))].getY() - 64;
				falling = false; //stop moving down
			}
			else
				falling = true; //ELSE KEEP FALLING
		}
		else { //ELSE (AS IN IF HE IS JUMPING OR RISING UP ELSEHOW) OR STILL
			falling = true; //KEEP "FALLING" (falling variables just means its applying gravity)
			if(tAbove.isSolid()||tAbove2.isSolid()) { //IF HE HITS CEILING
				//y=tAbove.getY()+32;
				yV = 0;
			}
		}
	}
	
	public void render(Graphics g) {		
		
		g.drawImage(ss.getSprite(headTile), x + (1-faceDirection)*16, y, faceDirection*32, 32, null);
		g.drawImage(ss.getSprite(headTile+8), x + (1-faceDirection)*16, y+32, faceDirection*32, 32, null);
		
		if(ghost.isDeployed()) {
			System.out.println("yes");
			tempGInfo = ghost.getInfoList().get(currGhostFrame);
			int gSpriteTile = chooseTileFrame(tempGInfo.getAction(), tempGInfo.getActionTimer());
			
			g.drawImage(ss.getSprite(gSpriteTile), tempGInfo.getLocation().x + (1-tempGInfo.getDirection())*16, tempGInfo.getLocation().y, tempGInfo.getDirection()*32, 32, null);
			g.drawImage(ss.getSprite(gSpriteTile+8), tempGInfo.getLocation().x + (1-tempGInfo.getDirection())*16, tempGInfo.getLocation().y+32, tempGInfo.getDirection()*32, 32, null);

		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.red);
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV-1), (int)(y + yV))].getR());								^
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV-1), (int)(y + yV + 32))].getR());							|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV-1), (int)(y + yV + 63))].getR());							|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV+33), (int)(y + yV))].getR());								|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV+33), (int)(y + yV + 32))].getR());							|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV+33), (int)(y + yV + 63))].getR());			COLLISION  BOUNDS TESTING
		//g2d.draw(tAbove.getR());														//						|
		//g2d.draw(tAbove2.getR());														//						|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV), (int)(y + yV + 64))].getR());			//				|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV+30), (int)(y + yV + 64))].getR());		//					|
		//g2d.drawLine(x, y+48, x+32, y+48);																	|
		//g2d.draw(getBounds());																				V
	}
	
	
	
	public int getSign(double a) {
		if(a>0)
			return 1;
		else if(a<0)
			return -1;
		
		return 0;
	}
	
	public void setDir(int pos, int bool) {
		keyDir[pos] = bool;
	}
	public int getDir(int pos) {
		return keyDir[pos];
	}
	
	public double getX() {return x;}
	public double getY() {return y;}
	public double getXV() {return xV;}
	public double getYV() {return yV;}
	public void setX(int x) {this.x=x;}
	public void setXV(double v) {this.xV=v;}
	public void setY(int y) {this.y=y;}
	public void setYV(double v) {this.yV=v;}
}
