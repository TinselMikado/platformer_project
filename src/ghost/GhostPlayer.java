package ghost;

import java.util.ArrayList;

public class GhostPlayer{
	
	ArrayList<GhostInfo> ghostInfoList;
	private boolean deployed = false;
	private int x, y;
	
	public GhostPlayer(int x, int y) {
		this.x = x;
		this.y = y;
		ghostInfoList = new ArrayList<>();
		
	}
	
	public ArrayList<GhostInfo> getInfoList(){
		return ghostInfoList;
	}
	
	public void setDeployed(boolean b) {
		deployed = b;
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
}
