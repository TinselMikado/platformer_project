package audio;

import java.util.HashMap;

public class AudioHandler {
	
	public static HashMap <String, AudioPlayer> audioMap;
	private int currMusic = 0;
	private int songListSize = 3;
	
	public AudioHandler(){
		audioMap = new HashMap<String, AudioPlayer>();
		
		audioMap.put("mainM0", new AudioPlayer("/resources/bellorz.wav"));
		audioMap.get("mainM0").volume.setValue(-15.0f);
		audioMap.put("mainM1", new AudioPlayer("/resources/yikesmyguy.wav"));
		audioMap.get("mainM1").volume.setValue(-23.0f);
		audioMap.get("mainM1").loopStart=352800;
		audioMap.put("mainM2", new AudioPlayer("/resources/Bsauce.wav"));
		audioMap.get("mainM2").volume.setValue(-22.0f);
		audioMap.get("mainM2").loopStart=651323;
		
		
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
		String s = "mainM";
		s+=Integer.toString(currMusic);
		audioMap.get(s).loopMusic(audioMap.get(s).loopStart, -1);
	}
	
	public void nextMusic() {
		stopAllMusic();
		currMusic++;
		currMusic%=songListSize;
		playMusic();
	}
	public void prevMusic() {
		stopAllMusic();
		currMusic--;
		if(currMusic<0)
			currMusic=songListSize-1;
		playMusic();
	}
	
	public void stopAllMusic() {
		String s = "mainM";
		for(int i = 0; i < songListSize; i++) {
			String ss = s+Integer.toString(i);
			audioMap.get(ss).stop();
		}
	}
	
	public void playSFX(String sfx) {
		audioMap.get(sfx).playSFX();
	}
	
	public void playSFXR(String sfx, int i) {
		sfx += Integer.toString(i);
		audioMap.get(sfx).playSFX();
	}
}
