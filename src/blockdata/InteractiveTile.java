package blockdata;

public class InteractiveTile extends Tile{

	private boolean state = false;
	private boolean naturalState;
	private byte triggerID;
	private boolean isSwitch = false;
	private byte stateTimer = 60;
	private boolean isSolidWhileOn;
	
	public InteractiveTile(int x, int y, int tileID, byte triggerID, boolean onAtStart) {
		super(x, y, tileID);
		this.triggerID = triggerID;
		naturalState = onAtStart;
		spriteNumber = state?tileID-1:tileID;
		switch(tileID) {
			case 15:
				isSolidWhileOn = true;
				break;
			case 17:
				isSolidWhileOn = false;
		}
	}
	
	public void setState(boolean s) {
		this.state = s;
		stateTimer = 0;
		setSprite(s);
		solid=!s||isSolidWhileOn;
	}	
	
	public byte getTimer() {
		return stateTimer;
	}
	
	public void setSprite(boolean b) {
		spriteNumber = b?tileID-1:tileID;
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
			setState(naturalState);
		}
	}
	
}