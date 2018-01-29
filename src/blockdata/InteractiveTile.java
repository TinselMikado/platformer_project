package blockdata;

public class InteractiveTile extends Tile{

	private boolean state = false;
	private boolean naturalState;
	private byte triggerID;
	private boolean isSwitch = false;
	private byte stateTimer = 60;
	
	public InteractiveTile(int x, int y, int tileID, byte triggerID, boolean onAtStart) {
		super(x, y, tileID);
		this.triggerID = triggerID;
		naturalState = onAtStart;
	}
	
	public void setState(boolean s) {
		this.state = s;
		stateTimer = 0;
		if(isSwitch)
			alterID(s);
	}	
	
	public byte getTimer() {
		return stateTimer;
	}
	
	public void alterID(boolean s) {
		LevelManager.update(this, s, x/32+(y/32)*40);		
	}
	
	public boolean getState() {
		return state;
	}
	
	public boolean getNatState() {
		return naturalState;
	}
	
	public void setSwitch(boolean b) {
		isSwitch = b;
	}
	
	public boolean isSwitch() {
		return isSwitch;
	}
	
	public byte getTrID() {
		return triggerID;
	}
	
	public void tick() {
		if(stateTimer<60)
			stateTimer++;
		else if(naturalState!=state) {
			state = naturalState;
			alterID(state);
		}
	}
	
}