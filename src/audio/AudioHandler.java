package audio;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class AudioHandler {
	
	public static HashMap <String, AudioPlayer> audioMap;
	
	public AudioHandler(){
		audioMap = new HashMap<String, AudioPlayer>();
		
		audioMap.put("mainM", new AudioPlayer("/resources/bellorz.wav"));
		audioMap.get("mainM").volume.setValue(-12.0f);
		
		audioMap.put("jump", new AudioPlayer("/resources/jump.wav"));
		audioMap.get("jump").volume.setValue(-12.0f);
		audioMap.put("dJump", new AudioPlayer("/resources/dJump.wav"));
		audioMap.get("dJump").volume.setValue(-28.0f);		
		
		audioMap.put("steps0", new AudioPlayer("/resources/steps0.wav"));
		audioMap.get("steps0").volume.setValue(-28.0f);
		audioMap.put("steps1", new AudioPlayer("/resources/steps1.wav"));
		audioMap.get("steps1").volume.setValue(-28.0f);
		audioMap.put("steps2", new AudioPlayer("/resources/steps2.wav"));
		audioMap.get("steps2").volume.setValue(-28.0f);
		audioMap.put("steps3", new AudioPlayer("/resources/steps3.wav"));
		audioMap.get("steps3").volume.setValue(-28.0f);
		
	}
	
	public void playMusic() {
		audioMap.get("mainM").playMusic();
	}
	
	public void playSFX(String sfx) {
		audioMap.get(sfx).playSFX();
	}
	
	public void playSFXR(String sfx, int i) {
		sfx += Integer.toString(i);
		audioMap.get(sfx).playSFX();
	}
}
