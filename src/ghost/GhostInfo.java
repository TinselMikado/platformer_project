package ghost;


public class GhostInfo {
	private int action, faceDirection, actionTimer, jumpType;
	private float xVel, yVel;
	
	public GhostInfo(int a, int fd, int at, int jT, float xx, float yy){
		action = a;
		faceDirection = fd;
		actionTimer = at;
		jumpType = jT;
		xVel = xx;
		yVel = yy;
	}
	public int getJumpType(){
		return jumpType;
	}
	
	public float getXVel() {
		return xVel;
	}
	
	public float getYVel() {
		return yVel;
	}
	
	
	public int getAction() {
		return action;
	}
	public int getActionTimer() {
		return actionTimer;
	}
	public int getDirection() {
		return faceDirection;
	}
}
