package ghost;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import audio.AudioHandler;
import blockdata.InteractiveTile;
import blockdata.LevelManager;
import blockdata.Tile;
import main.SpriteSheet;

public class GhostPlayer{

	ArrayList<GhostInfo> ghostInfoList;
	private boolean deployed = false;
	private int x, y;
	private double xV, yV;
	private int currGhostFrame = 0;
	private GhostInfo tempGInfo;
	private boolean finished = false;
	private Tile gtAbove, gtAbove2, gtBelow, gtBelow2;
	private Tile[] level;
	private AudioHandler audioHandler;
	private int ghostHeadTile;
	private SpriteSheet ss;
	private boolean falling;
	private Random r;
	private int action;
	private int prevAction;
	private HashMap<InteractiveTile, Byte> inTileMap;
	

	public GhostPlayer(int x, int y, LevelManager levelManager, AudioHandler ah, SpriteSheet ss) {
		this.x = x;
		this.y = y;
		ghostInfoList = new ArrayList<>();
		level = levelManager.getArray();
		audioHandler = ah;
		this.ss=ss;
		r = new Random();
		inTileMap = levelManager.getInTileMap();
	}

	int tickTimer = 0;
	int actionTimer = 0;
	
	public void tick() {

		actionTimer++;
		tickTimer++;
		tickTimer%=120;
		
		if(deployed&&!finished || (yV!=0||xV!=0)) {
			gtAbove  = level[xyCoordToTileSet(x,		(int)(y + yV -1))];
			gtBelow  = level[xyCoordToTileSet(x, 		(int)(y + yV + 64))];
			gtAbove2 = level[xyCoordToTileSet(x + 31,	(int)(y + yV -1))];
			gtBelow2 = level[xyCoordToTileSet(x + 31, 	(int)(y + yV + 64))];
		}

		if(getInfoList()!=null)
			tempGInfo = getInfoList().get(currGhostFrame);																//get information from array above

		if(!finished)
			xV = tempGInfo.getXVel()*checkGhostXBounds(getSign(tempGInfo.getXVel())); //check stored xvelocity for collision
		else xV=0;
		x+=xV;	//apply ghost x movement base on xvelocity

		falling = i2b(checkGhostYBounds(getSign(yV)));
		
		if(!finished) {
			if(falling) {
		

			if(yV<8)
				yV+=0.3*checkGhostYBounds(1);					//apply gravity to a max of 8
			else yV=8;

			if(!gtAbove.isSolid()&&!gtAbove2.isSolid()&&tempGInfo.getJumpType()==2) {			
				yV = -7;
			}

			}
			else {
				yV = tempGInfo.getJumpType()*-9;
			}
		}
		else if(falling){
			yV+=0.3*checkGhostYBounds(1);
		}
		
		yV = yV*checkGhostYBounds(getSign(yV));
		y+=yV;

		
		if(falling) {
			if(yV<0)
				action = 2;				//jumping
			else if(yV>6)
				action = 3;				//falling
			else
				action = 4;				//slow falling
		}else {
			if(xV==0)
				action = 0;
			else
				action = 1;
		}


		if(tempGInfo.getJumpType()==1)
			audioHandler.playSFX("jump");	//play jump sfx according to what kind of jump
		if(tempGInfo.getJumpType()==2)
			audioHandler.playSFX("dJump");


		if(currGhostFrame == getInfoList().size()-1) { 	//if ghost at the end of its path, set the ghost to "finished"
			finished = true;
		}
		
		ghostHeadTile = 32 + chooseTileFrame(action, actionTimer); // set sprite to action			

		if(!finished)
			currGhostFrame++; //continue on ghost path
		
		if(prevAction!=action)
			actionTimer = 0; 	//reset the frame position if playing a different animation (changing action)
		prevAction = action;		//set prevaction to allow above method to work
		
		
	}

	public int checkGhostXBounds(int dir){
		
		Tile tUP =   level[xyCoordToTileSet((int)(x+xV+16+19*dir), (int)(y + 0 ))]; //checks top corner, middle and bottom of player
		Tile tMID =  level[xyCoordToTileSet((int)(x+xV+16+19*dir), (int)(y + 32))]; //in given x direction
		Tile tDOWN = level[xyCoordToTileSet((int)(x+xV+16+19*dir), (int)(y + 63))];
		
		if (tUP.isSolid()||tMID.isSolid()||tDOWN.isSolid())	{ //if any 3 tilespaces are solid
			if(y+63<tDOWN.getY()+2*tDOWN.getHeight()) // if player is above the height of the bottom block, then walk
				return 1;
			return 0; // else, disallow the movement
		}
		
		return 1; //if none are solid, allow movement
	}
	
public int checkGhostYBounds(int dir) {
		
		if(dir>=0) { //IF CHAR IS FALLING DOWNWARDS OR STILL
			
						
			Class<? extends Tile> tBelowType = gtBelow.getClass();
			Class<? extends Tile> tBelow2Type = gtBelow2.getClass();
			
			if((gtBelow.isSolid()||gtBelow2.isSolid())) { //AND EITHER BLOCK UNDERNEATH IS SOLID	
				
				byte solid = (byte)(b2i(gtBelow.isSolid()) + 2*b2i(gtBelow2.isSolid())); //solid = 1 if tbelow solid, 2 if tbelow 2 solid, 3 if both
				
						
				switch(solid) {
				case 1:
					if(tBelowType==InteractiveTile.class&&((InteractiveTile)gtBelow).isSwitch()) {
						activateIntTile((InteractiveTile)gtBelow);  //checks for buttons and switches, activates them

					}
					if(y+64<gtBelow.getY()+gtBelow.getHeight()*2) {						
						y = gtBelow.getY()+gtBelow.getHeight()*2 - 64;
						
					}
					break;
				case 2:
					if(tBelow2Type==InteractiveTile.class&&((InteractiveTile)gtBelow2).isSwitch())
						activateIntTile((InteractiveTile)gtBelow2); //all this garbage also allows for varying 'heights' of blocks
					if(y+64<gtBelow2.getY()+gtBelow2.getHeight()*2) {						
						y = gtBelow2.getY()+gtBelow2.getHeight()*2 - 64;
						
					}
					break;
				case 3:
					boolean tBelowBigger = gtBelow.getHeight()<=gtBelow2.getHeight();
					Tile tt = tBelowBigger ? gtBelow:gtBelow2;
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
			if(gtAbove.isSolid()||gtAbove2.isSolid()) { //IF HE HITS CEILING
				y = gtAbove.getY()+32;
				return 0;
			}
		}
		return 1;
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
	
	public void activateIntTile(InteractiveTile it) {
		it.setState(true);
		for(InteractiveTile _it : inTileMap.keySet()) {
			if(_it.getTrID()==it.getTrID()) {
				_it.setState(true);
			}
		}
	}
	
	public void render(Graphics g) {		
		if(tempGInfo!=null){
			g.drawImage(ss.getSprite(ghostHeadTile), x + (1-tempGInfo.getDirection())*16, y, tempGInfo.getDirection()*32, 32, null); //else copy whatplayer did
			g.drawImage(ss.getSprite(ghostHeadTile+8), x + (1-tempGInfo.getDirection())*16, y+32, tempGInfo.getDirection()*32, 32, null);
		}
	}
	
	public int getSign(double a) {
		if(a>0)
			return 1;
		else if(a<0)
			return -1;
		return 0;
	}

	public ArrayList<GhostInfo> getInfoList(){
		return ghostInfoList;
	}

	public int xyCoordToTileSet(int x, int y) {
		return x/32 + (y/32)*40; //gets i value of whatever tile contains this x&y position
	}

	public void setDeployed(boolean b) {
		deployed = b;
	}
	
	public void setCurrGhostFrame(int i) {
		currGhostFrame = i;
	}
	
	public void setFinished(boolean b) {
		finished = b;
	}
	
	public int b2i(boolean b) {
		return b?1:0;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public int getX() {
		return x;
	}

	public float getXVel(int frame) {
		return ghostInfoList.get(frame).getXVel();
	}

	public float getYVel(int frame) {
		return ghostInfoList.get(frame).getYVel();
	}

	public int getY() {
		return y;
	}

	public void setX(int xx) {
		x=xx;
	}
	public void setY(int yy) {
		y=yy;;
	}
	
	public boolean i2b(int i) {
		if(i==1)
			return true;
		return false;
	}
}
