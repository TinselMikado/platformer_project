package input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import main.Game;

public class KbInput extends KeyAdapter{

	Game game;
	
	public KbInput(Game game) {
		this.game = game;
	}
	
	public void keyPressed(KeyEvent e) {
		game.keyPressed(e);
	}
	
	public void keyReleased(KeyEvent e) {
		game.keyReleased(e);
	}

}
