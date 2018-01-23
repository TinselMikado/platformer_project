package main;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Window{
	
	public Window(int w, int h, String title, Game game) {
		
		Dimension d = new Dimension(w, h);		
		JFrame frame = new JFrame(title);	
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(d);		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.setPreferredSize(d);
		game.setMinimumSize(d);
		game.setMaximumSize(d);
		game.setVisible(true);
		
		
		frame.add(game);
		frame.pack();
		
		game.requestFocus();
		
		
	}
}
