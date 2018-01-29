package audio;

import java.io.BufferedInputStream;

import javax.sound.sampled.*;

public class AudioPlayer {

	private Clip clip;
	public FloatControl volume;
	public int loopStart = 0;
	
	public AudioPlayer(String path){
		
		try {
			BufferedInputStream bufferedIn = new BufferedInputStream(getClass().getResourceAsStream(path)); //stores music as input stream in memory
			AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);								//specifies it as audio input stream
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),
					16,
					baseFormat.getChannels(),
					baseFormat.getChannels()*2, 
					baseFormat.getSampleRate(), 
					false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
			volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void playMusic() {
		if(clip == null) return;
		stop();
		clip.setFramePosition(0);
		clip.setLoopPoints(0, -1);
		clip.loop(Clip.LOOP_CONTINUOUSLY);		
	}
	
	public void loopMusic(int start, int end) {
		if(clip == null) return;
		stop();
		clip.setFramePosition(0);
		clip.setLoopPoints(start, end);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void playSFX() {
		stop();
		if(clip == null) return;
		clip.setFramePosition(0);
		clip.start();
		
	}
		
	public void stop() {
		if(clip.isRunning()) clip.stop();
	}
	
	public void close() {
		stop();
		clip.close();
	}
	
}