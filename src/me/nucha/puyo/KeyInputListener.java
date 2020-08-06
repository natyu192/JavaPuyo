package me.nucha.puyo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInputListener extends KeyAdapter {

	public static int fallMax = 5;
	public static int fallTimer = fallMax;
	public static boolean fall = false;

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 27) { // esc
			if (PuyoGame.screen == PuyoGame.SCREEN_PLAYING) {
				PuyoStage.clearStage();
				PuyoGame.screen = PuyoGame.SCREEN_TITLE;
				PuyoStage.gameState = PuyoStage.STATE_CONTROLLING;
			}
		}
		if (e.getKeyCode() == 32) { // space
			if (PuyoGame.screen == PuyoGame.SCREEN_TITLE) {
				PuyoStage.init();
				PuyoGame.screen = PuyoGame.SCREEN_PLAYING;
				return;
			}
			if (PuyoGame.screen == PuyoGame.SCREEN_PLAYING && PuyoStage.gameState == PuyoStage.STATE_GAMEOVER) {
				PuyoStage.clearStage();
				PuyoGame.screen = PuyoGame.SCREEN_TITLE;
				PuyoStage.gameState = PuyoStage.STATE_CONTROLLING;
			}
		}
		// System.out.println(e.getKeyCode());
		if (PuyoGame.screen == PuyoGame.SCREEN_PLAYING && PuyoStage.gameState == PuyoStage.STATE_CONTROLLING) {
			switch (e.getKeyCode()) {
			case 37: // 左矢印
				PuyoStage.getPuyoEntity().move(PuyoEntity.LEFT);
				break;
			case 39: // 右矢印
				PuyoStage.getPuyoEntity().move(PuyoEntity.RIGHT);
				break;
			case 40: // 下矢印
				if (fallTimer == 0) {
					fall = true;
					fallTimer = fallMax;
				} else {
					fall = false;
					fallTimer--;
				}
				break;
			case 88: // Xキー
				PuyoStage.getPuyoEntity().rotate(PuyoEntity.RIGHT);
				break;
			case 90: // Zキー
				PuyoStage.getPuyoEntity().rotate(PuyoEntity.LEFT);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (PuyoGame.screen == PuyoGame.SCREEN_PLAYING && PuyoStage.gameState == PuyoStage.STATE_CONTROLLING) {
			switch (e.getKeyCode()) {
			case 40: // 下矢印
				fall = false;
				fallTimer = fallMax;
				break;
			}
		}
	}
}
