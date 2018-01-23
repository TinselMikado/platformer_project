package input;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import main.Game;

public class MouseInput extends MouseInputAdapter{
	
	private Game game;
	
	public MouseInput(Game game) {
		this.game = game;
	}

	public void mousePressed(MouseEvent e) {
		int b = e.getButton();
		if(b==1)
			game.mousePressed(e);
		else if(b==3)
			game.printPlayerInfo();
	}
}
