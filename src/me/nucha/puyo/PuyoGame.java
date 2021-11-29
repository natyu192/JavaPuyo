package me.nucha.puyo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class PuyoGame extends Canvas implements Runnable {

	private static final long serialVersionUID = 1882977389986851652L;

	public static int screen = 0;
	public static final int SCREEN_TITLE = 0;
	public static final int SCREEN_PLAYING = 1;

	private Thread thread;
	private boolean running = false;

	public PuyoGame() {
		start();
		addKeyListener(new KeyInputListener());
	}

	public static void main(String[] args) {
		new PuyoGame();
	}

	public synchronized void start() {
		new GameWindow(PuyoConfig.WIDTH, PuyoConfig.HEIGHT, this);
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) { // 1秒に60回起きる
				tick();
				delta--;
			}
			if (running) {
				render();
			}
		}
		stop();
	}

	private void tick() {
		switch (screen) {
		case SCREEN_PLAYING:
			if (PuyoStage.gameState != PuyoStage.STATE_GAMEOVER) {
				PuyoStage.tick();
			}
			break;
		}
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		switch (screen) {
		case SCREEN_TITLE:
			g.setColor(Color.green);
			g.fillRect(0, 0, 30, PuyoConfig.HEIGHT);
			g.fillRect(PuyoConfig.WIDTH - 45, 0, 30, PuyoConfig.HEIGHT);
			g.fillRect(0, 0, PuyoConfig.WIDTH, 30);
			g.fillRect(0, PuyoConfig.HEIGHT - 60, PuyoConfig.WIDTH, 30);
			g.setFont(new Font("Lucida Console", Font.BOLD, 20));
			g.setColor(Color.black);
			g.drawString("PuyoPuyo", (PuyoConfig.WIDTH - g.getFontMetrics().stringWidth("PuyoPuyo")) / 2, PuyoConfig.HEIGHT / 4);
			g.drawString("Press SPACE to start!", (PuyoConfig.WIDTH - g.getFontMetrics().stringWidth("Press SPACE to start!")) / 2, PuyoConfig.HEIGHT / 4 + 30);

			g.dispose();
			bs.show();
			break;
		case SCREEN_PLAYING:
			g.setColor(Color.gray);
			g.fillRect(0, 0, PuyoConfig.WIDTH, PuyoConfig.HEIGHT);

			PuyoStage.render(g);

			g.dispose();
			bs.show();
			break;
		}
	}

}
