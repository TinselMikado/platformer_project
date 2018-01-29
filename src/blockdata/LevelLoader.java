package blockdata;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LevelLoader {
	private String lvlStr = "";
	
	public LevelLoader(String path){
		
		InputStream in = getClass().getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String l;
		try {
			while((l = reader.readLine())!=null) { //loads the level from a text file which contains a grid of ID numbers for each tile, lvlStr passed into LevelCreate
				lvlStr += l;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lvlStr = lvlStr.replaceAll("(\\n|\\r)", "");
		lvlStr = lvlStr.replaceAll(" ", "0");
		lvlStr = lvlStr.replaceAll("2|3|4|5|6|7|8|9", "1"); //removes corner pieces just to check collision shit
		
	}
	
	public String getLevelString() {
		return lvlStr;
	}
}
