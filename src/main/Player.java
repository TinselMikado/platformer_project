package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;

import audio.AudioHandler;
import blockdata.InteractiveTile;
import blockdata.LevelManager;
import blockdata.Tile;
import ghost.GhostInfo;
import ghost.GhostPlayer;

public class Player {
	
	private int x, y;
	private float attemptedxV, attemptedyV, xV, yV;
	public int action = 0; // 0 = idle, 1 = running, 2 = jumping, 3 = falling, 4 = slowfall
	private int faceDirection = 1; //-1 left, 1 right
	private int[] keyDir; //stores whether keys are held down, position 0 is VK_LEFT, 1:VK_UP, 2:VK_RIGHT, 3:VK_DOWN, 1 means true, 0 means false
	private boolean falling = false;
	private SpriteSheet ss;
	public int allowJump = 1;
	private int allowDJump = 0;
	private Tile[] level;
	private HashMap<InteractiveTile, Byte> inTileMap;
	public Tile tAbove, tBelow, tAbove2, tBelow2;
	private int headTile = 8;
	private int prevAct = 0;
	private AudioHandler audioHandler;
	private Random r;
	private GhostPlayer ghost;
	private int jumpType = 0;
	private LevelManager levelManager;
	private int sTile;
	
	public Player(SpriteSheet ss, LevelManager levelManager, AudioHandler ah) {
		
		keyDir = new int[4];
		this.ss=ss;
		audioHandler = ah;
		r  = new Random();
		
		this.levelManager = levelManager;		
		level = levelManager.getArray();
		inTileMap = levelManager.getInTileMap();
		sTile = levelManager.getSpawnTile();
		x = 32*(sTile%40); y = 32*(sTile/40);
		ghost = new GhostPlayer(x, y, levelManager, audioHandler, ss);
		
		tAbove = level[0];
		tBelow = level[0];
		tAbove2 = level[0];
		tBelow2 = level[0];
		
	}
	
	public void newLevel() {
		
		levelManager.initializeLevel(levelManager.getLevelNumber()+1);
		
		sTile = levelManager.getSpawnTile();
		System.out.println(sTile);
		level = levelManager.getArray();
		inTileMap = levelManager.getInTileMap();
		x = 32*(sTile%40); y = 32*(sTile/40);
		xV = 0;
		yV = 0;
		
		resetGhost();
	}
	
	int actionTimer = -1;
	int djumpTimer = 0;
	int tickTimer = 0;
	
	public void tick() {
		
		//System.out.println(y + " is y");
		
		tickTimer++;
		tickTimer%=120;
		actionTimer++;
		
		if(djumpTimer<30)
			djumpTimer++;			
		
		/*
		 * 
		 * 			VERTICAL BOUNDARY BOX SETTINGS
		 * 
		 */
				
		tAbove  = level[xyCoordToTileSet(x, (int)(y + yV - 1))];
		tBelow  = level[xyCoordToTileSet(x, (int)(y + yV + 64))];
		tAbove2 = level[xyCoordToTileSet(x + 31, (int)(y + yV - 1))];
		tBelow2 = level[xyCoordToTileSet(x + 31, (int)(y + yV + 64))];
				
		/*		
		*	
		*				X MOVEMENT CALCS
		*
		*/
		
		
		
		
			
		/*
		 * 
		 * 				FALLING, YMOVEMENT CALC, GRAVITY APPLIC., & JUMPING
		 * 		
		 */
	
		
		falling = i2b(checkYBounds(getSign(yV))); 	//checks whether character on the ground or not, sets falling to true/false
		
			// releasing the jump key while in the air will set it to 1, using the double jump sets allowDJump to -1 until player hits ground again
		
		
		if(falling) {
				
			allowJump = 0; 		//dont allow the player to (normal) jump while in the air
			
			if(allowDJump==0)						//DJump is set to zero when on the ground,
				allowDJump = 1 - keyDir[1]; 
			
			if(yV<8)
				yV+=0.3*checkYBounds(1);					//apply gravity to a max of 8
			else yV=8;
			
			if(!tAbove.isSolid()&&!tAbove2.isSolid()&&djumpTimer==30&&allowDJump==1&&keyDir[1]==1) {			
				dJump();
			}
			
			yV += attemptedyV*checkYBounds(getSign(attemptedyV));
		}
		else{
			yV=0;							//resets any left over gravity from last tick
			if(allowJump==0)
					allowJump = 1 - keyDir[1];
			allowDJump = 0;
			if(!tAbove.isSolid()&&!tAbove2.isSolid()&&allowJump==1&&keyDir[1]==1) { // if both blocks above are nonsolid, and player pressing jump, and char is allowed to jump, then jump
				jump();
			}
			yV = attemptedyV * checkYBounds(getSign(attemptedyV)); //apply Y velocity
			
		}
		
		
		
		attemptedxV = keyDir[0]*-3 + keyDir[2]*3; //attemps moving accoring to key input
		xV = checkXBounds(getSign(attemptedxV))*attemptedxV; //sets xvelocity to the dir of keys held down, if opposite directions both held down, vel = 0
		x+=xV; //move player on x by xV
		y+=yV; //apply Y movement
		attemptedyV=0; //reset attempted movement
		
		/*			
		 *  
		 * 				DIRECTION AND ACTION SETTING FOR ANIMATIONS
		 * 
		 */
		
		
		
		if(keyDir[0]==1&&keyDir[2]==0)
			faceDirection = -1;
		if(keyDir[2]==1&&keyDir[0]==0)
			faceDirection = 1;
		
		if(!falling&&attemptedxV==0)
			action = 0;					//idle
		
		if(!falling&&attemptedxV!=0)
			action = 1;					//walking
		
		if(falling) {
			if(yV<0)
				action = 2;				//jumping
			else if(yV>6)
				action = 3;				//falling
			else
				action = 4;				//slow falling
		}
		
		if(prevAct!=action)
			actionTimer = 0; 	//reset the frame position if playing a different animation (changing action)
		prevAct = action;		//set prevaction to allow above method to work
		
		headTile = chooseTileFrame(action, actionTimer); //choose the tile depending on what char is doing
		
		if(actionTimer>600&&action==0)
			action = 0; //change this value later, but idea is if character is idle 10seconds+ then do some 'waiting' animation
		
		
		
		/*
		 * 
		 * 
		 *					GHOST INFORMATION SETTING & PLAYBACK 
		 * 
		 * 
		 */
		
			
		
		if(!ghost.isDeployed()) {																		//if ghost is not "out"
			ghost.getInfoList().add(new GhostInfo(faceDirection, jumpType, attemptedxV, attemptedyV)); //store information about character to the ghost info array
		}
		else {
			ghost.tick();
		}
		for(InteractiveTile _it: inTileMap.keySet()) {
			_it.tick();
		}
		jumpType = 0; //reset jump type
		
	}
	
	public void jump() {
		yV=0;
		attemptedyV = -9;
		audioHandler.playSFX("jump");
		allowJump=0;
		djumpTimer = 0;
		jumpType = 1;
	}
	
	public void dJump() {
		yV=0;
		attemptedyV = -7;
		allowDJump = -1;
		audioHandler.playSFX("dJump");
		jumpType = 2;
	}
	
	public void deployGhost() {
		ghost.setDeployed(true);
		ghost.setCurrGhostFrame(0);
	}
	
	public GhostPlayer getGhost() {
		return ghost;
	}
	
	public void resetGhost() {
		ghost.setFinished(false);
		ghost.setDeployed(false);
		ghost.setCurrGhostFrame(0); //not sure if this is needed but just in case
		ghost = new GhostPlayer(x, y, levelManager, audioHandler, ss);
	}
	
	
	public int chooseTileFrame(int act, int time) { //returns tile of head, body is just +8 (added in render function)
		
		//System.out.println(((InteractiveTile)level[946]).getTimer());
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
		
		Tile tUP =   level[xyCoordToTileSet((int)(x+xV+16+19*dir), (int)(y + 0 ))]; //checks top corner, middle and bottom of player
		Tile tMID =  level[xyCoordToTileSet((int)(x+xV+16+19*dir), (int)(y + 32))]; //in given x direction
		Tile tDOWN = level[xyCoordToTileSet((int)(x+xV+16+19*dir), (int)(y + 63))];
		
		if(tUP.getID()==11||tMID.getID()==11||tDOWN.getID()==11) {
			newLevel();
			return 0;
		}
		
		if (tUP.isSolid()||tMID.isSolid()||tDOWN.isSolid())	{ //if any 3 tilespaces are solid
			if(y+63<tDOWN.getY()+2*tDOWN.getHeight()) // if player is above the height of the bottom block, then walk
				return 1;
			return 0; // else, disallow the movement
		}
		
		return 1; //if none are solid, allow movement
	}
	
	
	public int checkYBounds(int dir) {
		
		if(dir>=0) { //IF CHAR IS FALLING DOWNWARDS OR STILL
			
						
			Class<? extends Tile> tBelowType = tBelow.getClass();
			Class<? extends Tile> tBelow2Type = tBelow2.getClass();
			
			if((tBelow.isSolid()||tBelow2.isSolid())) { //AND EITHER BLOCK UNDERNEATH IS SOLID	
				
				byte solid = (byte)(b2i(tBelow.isSolid()) + 2*b2i(tBelow2.isSolid())); //solid = 1 if tbelow solid, 2 if tbelow 2 solid, 3 if both
				
						
				switch(solid) {
				case 1:
					if(tBelowType==InteractiveTile.class&&((InteractiveTile)tBelow).isSwitch()) {
						activateIntTile((InteractiveTile)tBelow);  //checks for buttons and switches, activates them

					}
					if(y+64<tBelow.getY()+tBelow.getHeight()*2) {						
						y = tBelow.getY()+tBelow.getHeight()*2 - 64;
						
					}
					break;
				case 2:
					if(tBelow2Type==InteractiveTile.class&&((InteractiveTile)tBelow2).isSwitch())
						activateIntTile((InteractiveTile)tBelow2); //all this garbage also allows for varying 'heights' of blocks
					if(y+64<tBelow2.getY()+tBelow2.getHeight()*2) {						
						y = tBelow2.getY()+tBelow2.getHeight()*2 - 64;
						
					}
					break;
				case 3:
					boolean tBelowBigger = tBelow.getHeight()<=tBelow2.getHeight();
					Tile tt = tBelowBigger ? tBelow:tBelow2;
					if(tt.getClass()==InteractiveTile.class&&((InteractiveTile)tt).isSwitch()) //if standing across 2 blocks set floor to higher one
						activateIntTile((InteractiveTile)tt);
					if(y+64<tt.getY()+tt.getHeight()*2) {						
						y = tt.getY()+tt.getHeight()*2 - 64;
						
					}
				}					
					return 0;
			}
			else
				return 1; //ELSE KEEP FALLING
		}
		else { //ELSE (AS IN IF HE IS JUMPING OR RISING UP ELSEHOW) OR STILL
			//falling = true; //KEEP "FALLING" (falling variables just means its applying gravity)
			if(tAbove.isSolid()||tAbove2.isSolid()) { //IF HE HITS CEILING
				y = tAbove.getY()+32;
				yV = 0;
				return 1;
			}
		}
		return 1;
	}
	
	public void activateIntTile(InteractiveTile it) {		

		it.setState(true);
		for(InteractiveTile _it : inTileMap.keySet()) {
			if(_it.getTrID()==it.getTrID()) {
				_it.setState(true);
			}
		}
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
	
	
	
	
	public void render(Graphics g) {		
				
		g.drawImage(ss.getSprite(headTile), x + (1-faceDirection)*16, y, faceDirection*32, 32, null);	// draw player head
		g.drawImage(ss.getSprite(headTile+8), x + (1-faceDirection)*16, y+32, faceDirection*32, 32, null); //draw player body
		
		if(ghost.getInfoList()!=null) {

			if(ghost.isDeployed()) { //if ghost is out,=
				ghost.render(g);				
			}
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.red);
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV-1), (int)(y + yV))].getR());								^
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV-1), (int)(y + yV + 32))].getR());							|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV-1), (int)(y + yV + 63))].getR());							|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV+33), (int)(y + yV))].getR());								|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV+33), (int)(y + yV + 32))].getR());							|
		//g2d.draw(level[xyCoordToTileSet((int)(x+xV+33), (int)(y + yV + 63))].getR());			COLLISION  BOUNDS TESTING
		g2d.draw(tBelow.getR());			
		g2d.draw(tBelow2.getR());							
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
	
	public int b2i(boolean b) {
		return b?1:0;
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
