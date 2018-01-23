package ghost;

import java.util.ArrayList;

public class GhostPlayer {
	
	ArrayList<GhostInfo> ghostInfoList;
	private boolean deployed = false;
	
	public GhostPlayer() {
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
}
