package ghost;

import main.Tuple;

public class GhostInfo {
	Tuple location;
	int action;
	int faceDirection;
	int actionTimer;
	int jumpType;
	
	public GhostInfo(Tuple t, int a, int fd, int at, int jT ){
		location = t;
		action = a;
		faceDirection = fd;
		actionTimer = at;
		jumpType = jT;
	}
	public int getJumpType(){
		return jumpType;
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
	public Tuple getLocation() {
		return location;
	}
}
