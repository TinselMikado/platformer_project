package ghost;


public class GhostInfo {
	private int faceDirection, jumpType;
	private float xVel, yVel;
	
	public GhostInfo(int fd, int jT, float xx, float yy){
		faceDirection = fd;
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
	public int getDirection() {
		return faceDirection;
	}
}
