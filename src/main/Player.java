package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import audio.AudioHandler;
import blockdata.Tile;
import ghost.GhostInfo;
import ghost.GhostPlayer;

public class Player {
	
	private int x, y;
	private float attemptedxV, attemptedyV, xV, yV;
	private int action = 0; // 0 = idle, 1 = running, 2 = jumping, 3 = falling, 4 = slowfall
	private int faceDirection = 1; //-1 left, 1 right
	private int[] keyDir; //stores whether keys are held down, position 0 is VK_LEFT, 1:VK_UP, 2:VK_RIGHT, 3:VK_DOWN, 1 means true, 0 means false
	private boolean falling = false;
	private SpriteSheet ss;
	public int allowJump = 1;
	private int allowDJump = 0;
	private Tile[] level;
	private Tile tAbove, tBelow, tAbove2, tBelow2, gtAbove, gtBelow, gtAbove2, gtBelow2;
	private int headTile = 8;
	private int prevAct = 0;
	private AudioHandler audioHandler;
	private Random r;
	private GhostPlayer ghost;
	private int currGhostFrame = 0;
	private int jumpType = 0;
	private GhostInfo tempGInfo;
	private boolean ghostFinished = false;
	private int ghostSpriteTile = 0;
	
	public Player(int x, int y, SpriteSheet ss, Tile[] level, AudioHandler ah) {
		
		this.x = x; this.y = y;
		keyDir = new int[4];
		this.ss=ss;
		this.level = level;
		audioHandler = ah;
		r  = new Random();
		ghost = new GhostPlayer(x, y);
		
		tAbove = level[0];
		tBelow = level[0];
		tAbove2 = level[0];
		tBelow2 = level[0];
		gtAbove = level[0];
		gtBelow = level[0];
		gtAbove2 = level[0];
		gtBelow2 = level[0];
		
	}
	
	int actionTimer = -1;
	int djumpTimer = 0;
	int tickTimer = 0;
	
	public void tick() {
		
				
		tickTimer++;
		tickTimer%=120;
		actionTimer++;
		
		if(djumpTimer<30)
			djumpTimer++;			
		
		tAbove = level[xyCoordToTileSet(x+2, (int)(y + yV))];
		tBelow = level[xyCoordToTileSet(x+2, (int)(y + yV + 64))];
		tAbove2 = level[xyCoordToTileSet(x+30, (int)(y + yV))];
		tBelow2 = level[xyCoordToTileSet(x+30, (int)(y + yV + 64))];
			
		if(ghost.isDeployed()&&(!ghostFinished || (ghost.getYVel(currGhostFrame)!=0||ghost.getXVel(currGhostFrame)!=0))) {
			gtAbove = level[xyCoordToTileSet(ghost.getX()+2, (int)(ghost.getY() + ghost.getYVel(currGhostFrame)))];
			gtBelow = level[xyCoordToTileSet(ghost.getX()+2, (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 64))];
			gtAbove2 = level[xyCoordToTileSet(ghost.getX()+30, (int)(ghost.getY() + ghost.getYVel(currGhostFrame)))];
			gtBelow2 = level[xyCoordToTileSet(ghost.getX()+30, (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 64))];
		}
		
				
		attemptedxV = keyDir[0]*-3 + keyDir[2]*3; //attemps moving accoring to key input
		xV = checkXBounds(getSign(attemptedxV))*attemptedxV; //sets xvelocity to the dir of keys held down, if opposite directions both held down, vel = 0
		
		if(keyDir[0]==1&&keyDir[2]==0)
			faceDirection = -1;
		if(keyDir[2]==1&&keyDir[0]==0)
			faceDirection = 1;
		
		if(!falling&&attemptedxV==0)
			action = 0;
		
		if(!falling&&attemptedxV!=0)
			action = 1;
			
		
		
		x+=xV; //move player on x by xV
				
		
		falling = i2b(checkYBounds(getSign(yV))); //checks whether character on the ground or not, sets falling to true/false
		
		if(falling) {
			if(yV<0)
				action = 2; //set action to 'jump' action
			else if(yV>6)
				action = 3; //set action to 'fall' action
			else
				action = 4; //set action to slow fall
			allowJump = 0;
			if(yV<8)
				attemptedyV+=0.3;	//apply gravity
			if(allowDJump==0)
				allowDJump = 1 - keyDir[1];
		}
		else{
			attemptedyV=0;
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
		
		yV = attemptedyV * checkYBounds(getSign(attemptedyV));
		y+=yV;
		
		if(prevAct!=action)
			actionTimer = 0; //reset the frame position if playing a different animation (changing action)
		
		headTile = chooseTileFrame(action, actionTimer); //choose the tile depending on what char is doing
		prevAct = action;
		if(actionTimer>600&&action==0)
			action = 0; //change this value later, but idea is if character is idle 10seconds+ then do some 'waiting' animation
		
			
		
		if(!ghost.isDeployed()) {
				ghost.getInfoList().add(new GhostInfo(action, faceDirection, actionTimer, jumpType, attemptedxV, attemptedyV));
		}
		else {

			tempGInfo = ghost.getInfoList().get(currGhostFrame);
			
			ghost.setX((int)(ghost.getX()+tempGInfo.getXVel()*checkGhostXBounds(getSign(tempGInfo.getXVel()))));
			if(ghost.getYVel(currGhostFrame)>=0&&(gtBelow.isSolid()||gtBelow2.isSolid())&&(ghost.getY()+64<gtBelow.getY()))
				ghost.setY(level[xyCoordToTileSet(0, (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 64))].getY() - 64);
			else
				ghost.setY((int)(ghost.getY()+tempGInfo.getYVel()*checkGhostYBounds()));
			
			checkGhostYBounds();
			
			if(tempGInfo.getJumpType()==1)
				audioHandler.playSFX("jump");
			if(tempGInfo.getJumpType()==2)
				audioHandler.playSFX("dJump");
			
			
			if(currGhostFrame == ghost.getInfoList().size()-1) {
				ghostFinished = true;
				ghostSpriteTile = 32 + tickTimer/60;
			}
			else {
				ghostSpriteTile = 32 + chooseTileFrame(tempGInfo.getAction(), tempGInfo.getActionTimer());
			}	
			
			if(!ghostFinished)
				currGhostFrame++;
		}
		
		jumpType = 0;
		
	}
	
	public void jump() {
		attemptedyV = -9; //jump
		audioHandler.playSFX("jump");
		allowJump=0;
		djumpTimer = 0;
		jumpType = 1;
	}
	
	public void dJump() {
		attemptedyV = -7; //jump
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
	
	public void resetGhost() {
		ghostFinished=false;
		ghost.setDeployed(false);
		currGhostFrame = 0;
		ghost = new GhostPlayer(x, y);
	}
	
	
	public int chooseTileFrame(int act, int time) { //returns tile of head, body is just +8 (added in render function)
				
		switch (act){
			case 0: //idle
				return 0 + (time/60)%2;
				
			case 1: //walking
				if((time-12)%36==0) { //if character is on step specific frame						
					audioHandler.playSFXR("steps", r.nextInt(4));
				}
						
					
				return 2 + (time/12)%6; //example: 10 is base tile (frame 0), time / {12} because tile changes every {12} ticks, %<6> because there are <6> total tiles
			case 2: //jumping
				return 16 + (time/12)%4;
			case 3: //falling
				return 22 + (time/12)%2;
			case 4: //slow falling
				return 20 + (time/12)%2;
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
				level[xyCoordToTileSet((int)(x+xV-4), (int)(y + yV + 60))].isSolid())	
				return 0; // if a solid block is found, disallow the movement
		}
		else if(dir==1){ //check right side
			if (level[xyCoordToTileSet((int)(x+xV+36), (int)(y + yV + 0 ))].isSolid() ||
				level[xyCoordToTileSet((int)(x+xV+36), (int)(y + yV + 32))].isSolid()|| //checks all 3 possible tiles covering right side for solid tiles
				level[xyCoordToTileSet((int)(x+xV+36), (int)(y + yV + 60))].isSolid()) {
				return 0; // if a solid block is found, disallow the movement
			}
		}
		
		return 1; //no solid blocks found, allow the movement
	}
	
	public int checkGhostXBounds(int dir){
		if(dir==-1) { //check left side
			if (level[xyCoordToTileSet((int)(ghost.getX()+ghost.getXVel(currGhostFrame)-4), (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 0 ))].isSolid() ||  //checks all 3 possible tiles covering left side for solid tiles
				level[xyCoordToTileSet((int)(ghost.getX()+ghost.getXVel(currGhostFrame)-4), (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 32))].isSolid() ||
				level[xyCoordToTileSet((int)(ghost.getX()+ghost.getXVel(currGhostFrame)-4), (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 60))].isSolid())
				return 0; // if a solid block is found, disallow the movement
		}
		else if(dir==1){ //check right side
			if (level[xyCoordToTileSet((int)(ghost.getX()+ghost.getXVel(currGhostFrame)+36), (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 0 ))].isSolid() ||
				level[xyCoordToTileSet((int)(ghost.getX()+ghost.getXVel(currGhostFrame)+36), (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 32))].isSolid()|| //checks all 3 possible tiles covering right side for solid tiles
				level[xyCoordToTileSet((int)(ghost.getX()+ghost.getXVel(currGhostFrame)+36), (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 60))].isSolid()) {
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
	
	
	public boolean checkFalling() {
		if(yV>=0) { //IF CHAR IS FALLING DOWNWARDS OR STILL
			if((tBelow.isSolid()||tBelow2.isSolid())) { //AND EITHER BLOCK UNDERNEATH IS SOLID	
				return false; //stop moving down
			}
			else
				return true; //ELSE KEEP FALLING
		}
		else { //ELSE (AS IN IF HE IS JUMPING OR RISING UP ELSEHOW) OR STILL
			//falling = true; //KEEP "FALLING" (falling variables just means its applying gravity)
			if(tAbove.isSolid()||tAbove2.isSolid()) { //IF HE HITS CEILING
				return false;
			}
		}
		return true;
	}
	
	public int checkYBounds(int dir) {
				
		if(dir>=0) { //IF CHAR IS FALLING DOWNWARDS OR STILL
			if((tBelow.isSolid()||tBelow2.isSolid())) { //AND EITHER BLOCK UNDERNEATH IS SOLID				
				if(y+64<tBelow.getY()) {
					attemptedyV = level[xyCoordToTileSet(0, (int)(y + yV + 64))].getY() - 64 - y;
					return 1; //snap to floor
				} else
					return 0;
			}
			else
				return 1; //ELSE KEEP FALLING
		}
		else { //ELSE (AS IN IF HE IS JUMPING OR RISING UP ELSEHOW) OR STILL
			//falling = true; //KEEP "FALLING" (falling variables just means its applying gravity)
			if(tAbove.isSolid()||tAbove2.isSolid()) { //IF HE HITS CEILING
				attemptedyV = 0;
				return 1;
			}
		}
		return 1;
	}
	
	public int checkGhostYBounds() {
		
		if(ghost.getYVel(currGhostFrame)>=0) { //IF GHOST IS FALLING DOWNWARDS OR STILL
			if((gtBelow.isSolid()||gtBelow2.isSolid())) { //AND EITHER BLOCK UNDERNEATH IS SOLID	
				if(ghost.getY()+64<gtBelow.getY())
					ghost.setY(level[xyCoordToTileSet(0, (int)(ghost.getY() + ghost.getYVel(currGhostFrame) + 64))].getY() - 64);
				
				return 0;
			}
			else
				return 1; //ELSE KEEP FALLING
		}
		else { //ELSE (AS IN IF HE IS JUMPING OR RISING UP ELSEHOW) OR STILL
			//falling = true; //KEEP "FALLING" (falling variables just means its applying gravity)
			if(gtAbove.isSolid()||gtAbove2.isSolid()) { //IF HE HITS CEILING
				return 0;
			}
		}
		return 1;
	}
	
	public void render(Graphics g) {		
		
		
		
		g.drawImage(ss.getSprite(headTile), x + (1-faceDirection)*16, y, faceDirection*32, 32, null);	// draw player head
		g.drawImage(ss.getSprite(headTile+8), x + (1-faceDirection)*16, y+32, faceDirection*32, 32, null); //draw player body
		
		if(tempGInfo!=null) {

			if(ghost.isDeployed()) { //if ghost is out,=
				Tuple gLoc = new Tuple(ghost.getX(), ghost.getY());
				

				if(ghostFinished) {
					int t =(int) ((System.currentTimeMillis() / 500) % 2);

					g.drawImage(ss.getSprite(32 + t), gLoc.x + (1-tempGInfo.getDirection())*16, gLoc.y, tempGInfo.getDirection()*32, 32, null); //if ghost at end of path, do idle animation
					g.drawImage(ss.getSprite(40 + t), gLoc.x + (1-tempGInfo.getDirection())*16, gLoc.y+32, tempGInfo.getDirection()*32, 32, null); //if ghost at end of path, do idle animation

				}
				else {
					g.drawImage(ss.getSprite(ghostSpriteTile), gLoc.x + (1-tempGInfo.getDirection())*16, gLoc.y, tempGInfo.getDirection()*32, 32, null); //else copy whatplayer did
					g.drawImage(ss.getSprite(ghostSpriteTile+8), gLoc.x + (1-tempGInfo.getDirection())*16, gLoc.y+32, tempGInfo.getDirection()*32, 32, null);
				}
			}
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.red);
		g2d.draw(gtBelow.getR());
		g2d.draw(gtBelow2.getR());
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
	
	public boolean i2b(int a) {
		if(a==1)
			return true;
		return false;
		
	}
	
	public void setDir(int pos, int bool) {
		keyDir[pos] = bool;
	}
	public int getDir(int pos) {
		return keyDir[pos];
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	public double getXV() {return xV;}
	public double getYV() {return yV;}
	public void setX(int x) {this.x=x;}
	public void setXV(float v) {this.xV=v;}
	public void setY(int y) {this.y=y;}
	public void setYV(float v) {this.yV=v;}

	public SpriteSheet getSpriteSheet() {
		return ss;
	}

	public Tile[] getLevel() {
		return level;
	}

	public AudioHandler getAudioHandler() {
		return audioHandler;
	}
}
