package me.nucha.puyo;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class GameWindow extends Canvas {

	private static final long serialVersionUID = 5368315217314441933L;

	public GameWindow(int width, int height, PuyoGame game) {
		JFrame frame = new JFrame("ぷよぷよ");

		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.setVisible(true);
	}

}
