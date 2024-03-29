package me.nucha.puyo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PuyoStage {

	public static final int STATE_CONTROLLING = 0;
	public static final int STATE_FALLING = 1;
	public static final int STATE_CLEARING = 2;
	public static final int STATE_GAMEOVER = 3;
	public static int gameState = STATE_CONTROLLING;

	public static StageObject[][] objects = new StageObject[PuyoConfig.STAGE_WIDTH][PuyoConfig.STAGE_HEIGHT];

	private static Random r = new Random();
	private static final Color[] puyoColors = new Color[] {
			new Color(217, 82, 82), // red
			new Color(82, 145, 217), // blue
			new Color(96, 217, 82),
			new Color(150, 0, 150)
	};
	private static PuyoEntity puyoNext;
	private static PuyoEntity puyoNext2;
	private static PuyoEntity puyoEntity;
	private static int puyoFallTimer = PuyoConfig.INTERVAL_NATURAL_FALL;
	private static int puyoCheckFallTimer = PuyoConfig.INTERVAL_FALL;
	private static int puyoFallFails = PuyoConfig.MAX_FAIL_TO_FALL;

	public static int score;

	public static void init() {
		// いろんな値を初期化する
		gameState = STATE_CONTROLLING;
		puyoFallTimer = PuyoConfig.INTERVAL_NATURAL_FALL;
		puyoCheckFallTimer = PuyoConfig.INTERVAL_FALL;
		puyoFallFails = PuyoConfig.MAX_FAIL_TO_FALL;
		score = 0;
		// すべて削除したあと壁を配置
		for (int x = 0; x < PuyoConfig.STAGE_WIDTH; x++) {
			for (int y = 0; y < PuyoConfig.STAGE_HEIGHT; y++) {
				objects[x][y] = null;
				if (x == 0 || x == PuyoConfig.STAGE_WIDTH - 1 || y == PuyoConfig.STAGE_HEIGHT - 1) {
					objects[x][y] = new StageObject();
				}
			}
		}
		// 最初のぷよをスポーンさせる
		puyoEntity = createPuyoEntity();
		puyoEntity.spawn();
		// 次のぷよを定義
		puyoNext = createPuyoEntity();
		// 次の次のぷよを定義
		puyoNext2 = createPuyoEntity();
	}

	public static void clearStage() {
		for (int x = 0; x < PuyoConfig.STAGE_WIDTH; x++) {
			for (int y = 0; y < PuyoConfig.STAGE_HEIGHT; y++) {
				objects[x][y] = null;
			}
		}
		puyoEntity = null;
		puyoNext = null;
		puyoNext2 = null;
	}

	public static void tick() {
		if (gameState == STATE_CONTROLLING) {
			if (puyoFallTimer == 0) {
				puyoFallTimer = PuyoConfig.INTERVAL_NATURAL_FALL;
				if (!puyoEntity.fall()) { // 地面があったら
					puyoFallFails--;
					if (puyoFallFails == 0) { // ２回失敗したら
						objects[puyoEntity.getMainX()][puyoEntity.getMainY()] = puyoEntity.getPuyoMain();
						objects[puyoEntity.getSubX()][puyoEntity.getSubY()] = puyoEntity.getPuyoSub(); // error
						puyoFallFails = PuyoConfig.MAX_FAIL_TO_FALL;
						gameState = STATE_FALLING;
					}
				}
			}
			puyoFallTimer--;
			return;
		}
		if (gameState == STATE_FALLING) {
			if (puyoCheckFallTimer == 0) {
				puyoCheckFallTimer = PuyoConfig.INTERVAL_FALL;
				boolean puyoFalled = false;
				puyoEntity = puyoNext;
				for (int x = PuyoConfig.STAGE_WIDTH; x > 0; x--) { // 下からチェックするお
					for (int y = PuyoConfig.STAGE_HEIGHT; y > 0; y--) {
						StageObject puyo = getStageObjectAt(x, y);
						if (puyo == null || !puyo.isPuyo()) {
							continue;
						}
						StageObject below = getStageObjectAt(x, y + 1);
						if (below == null) {
							objects[x][y] = null;
							objects[x][y + 1] = puyo;
							puyoFalled = true;
						}
					}
				}
				if (!puyoFalled) { // これ以上落下できなかったら次は削除確認
					gameState = STATE_CLEARING;
				}
			}
			puyoCheckFallTimer--;
		}
		if (gameState == STATE_CLEARING) {
			int[][] direction = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
			Set<int[]> puyosToRemove = new HashSet<>();
			Set<int[]> puyosChecked = new HashSet<>();
			for (int x = PuyoConfig.STAGE_WIDTH; x > 0; x--) { // 下からチェックするお
				for (int y = PuyoConfig.STAGE_HEIGHT; y > 0; y--) {
					StageObject puyo = getStageObjectAt(x, y);
					if (puyo == null || !puyo.isPuyo()) { // ぷよ以外だったら戻る
						continue;
					}
					// objects[x][y]に連結している同じ色のぷよの座標の一覧を取得
					Set<int[]> theChain = getChain(x, y, direction, puyo, null, puyosToRemove, null);
					if (theChain.size() >= PuyoConfig.MIN_PUYOS_TO_REMOVE) {
						puyosToRemove.addAll(theChain);
					}
					puyosChecked.addAll(theChain);
				}
			}
			if (puyosToRemove.size() > 0) {
				for (int[] coord : puyosToRemove) {
					objects[coord[0]][coord[1]] = null;
				}
				gameState = STATE_FALLING;
			} else {
				if (getStageObjectAt(PuyoConfig.START_X, PuyoConfig.START_Y) != null) {
					gameState = STATE_GAMEOVER;
					return;
				}
				puyoNext = puyoNext2;
				puyoNext2 = createPuyoEntity();
				puyoEntity.spawn();
				gameState = STATE_CONTROLLING;
			}
		}
	}

	private static Set<int[]> getChain(int x, int y, int[][] direction, StageObject puyo, Set<int[]> puyosChecked, Set<int[]> puyosToRemove,
			Set<int[]> theChain) {
		if (theChain == null) {
			theChain = new HashSet<>();
		}
		if (theChain.contains(new int[] { x, y })) {
			theChain.add(new int[] { x, y });
		}
		if (puyosChecked == null) {
			puyosChecked = new HashSet<>();
		}
		for (int i = 0; i < direction.length; i++) {
			int sideX = x + direction[i][0];
			int sideY = y + direction[i][1];
			if (containsChain(puyosChecked, new int[] { sideX, sideY })) {
				continue;
			}
			StageObject sidePuyo = getStageObjectAt(sideX, sideY);
			// チェック済みリストに追加
			puyosChecked.add(new int[] { sideX, sideY });
			if (sidePuyo == null || !sidePuyo.isPuyo()) { // ぷよ以外は戻る
				continue;
			}
			if (puyo.getColor() == sidePuyo.getColor()) { // 同じ色だったら
				// チェーンに追加して
				theChain.add(new int[] { sideX, sideY });
				// その隣もチェックしてみる（再帰処理）
				theChain = getChain(sideX, sideY, direction, sidePuyo, puyosChecked, puyosToRemove, theChain);
			}
		}
		return theChain;
	}

	/**
	 * 「2個のint型」の配列のSetに、「2個のint型」が同じ順番で含まれているかをチェックします
	 * 「2個のint型」の例: new int[] { 0, 1 }
	 *
	 * @param chains       「2個のint型」の配列
	 * @param anotherChain 「2個のint型」
	 * @return anotherChainがchainsに含まれている場合はtrue, それ以外はfalseを返します
	 */

	private static boolean containsChain(Set<int[]> chains, int[] anotherChain) {
		for (int[] chain : chains) {
			if (chain[0] == anotherChain[0]
					&& chain[1] == anotherChain[1]) {
				return true;
			}
		}
		return false;
	}

	public static void render(Graphics g) {
		for (int x = 0; x < PuyoConfig.STAGE_WIDTH; x++) {
			for (int y = 0; y < PuyoConfig.STAGE_HEIGHT; y++) {
				StageObject object = objects[x][y];
				if (object == null) {
					continue;
				}
				drawStageObject(x, y, object, object.isPuyo(), g);
			}
		}
		if (gameState == STATE_CONTROLLING && puyoEntity != null) {
			drawStageObject(puyoEntity.getMainX(), puyoEntity.getMainY(), puyoEntity.getPuyoMain(), true, g);
			drawStageObject(puyoEntity.getSubX(), puyoEntity.getSubY(), puyoEntity.getPuyoSub(), true, g);
		}
		if (puyoNext != null) {
			drawStageObject(PuyoConfig.NEXT_X, PuyoConfig.NEXT_Y + 1, puyoNext.getPuyoMain(), true, g);
			drawStageObject(PuyoConfig.NEXT_X, PuyoConfig.NEXT_Y, puyoNext.getPuyoSub(), true, g);
		}
		if (puyoNext2 != null) {
			drawStageObject(PuyoConfig.NEXT_X + 1, PuyoConfig.NEXT_Y + 1 + 1, puyoNext2.getPuyoMain(), true, g);
			drawStageObject(PuyoConfig.NEXT_X + 1, PuyoConfig.NEXT_Y + 1, puyoNext2.getPuyoSub(), true, g);
		}
		if (PuyoStage.gameState == PuyoStage.STATE_GAMEOVER) {
			g.setFont(new Font("Lucida Console", Font.BOLD, 20));
			g.setColor(Color.yellow);
			g.drawString("GAME OVER!", (PuyoConfig.WIDTH - g.getFontMetrics().stringWidth("GAME OVER!")) / 2, PuyoConfig.HEIGHT / 4);
			g.drawString("Press SPACE", (PuyoConfig.WIDTH - g.getFontMetrics().stringWidth("Press SPACE")) / 2, PuyoConfig.HEIGHT / 4 + 30);
			g.drawString("to back to title screen", (PuyoConfig.WIDTH - g.getFontMetrics().stringWidth("to back to title screen")) / 2,
					PuyoConfig.HEIGHT / 4 + 60);
		}
	}

	public static void drawStageObject(int x, int y, StageObject object, boolean isPuyo, Graphics g) {
		int startDrawX = x * 30;
		int startDrawY = y * 30 - 30;
		if (isPuyo) {
			int thickness = 3; // 縁の太さ
			int offset = 2; // 削る周りのドット数
			/*int corner = 5; // 削る角のドット数
			// 二本の太い線をクロスさせて丸っぽく見せかける
			g.fillRect(startDrawX + offset + corner, startDrawY + offset, 30 - offset * 2 - corner * 2, 30 - offset * 2); // 縦
			g.fillRect(startDrawX + offset, startDrawY + offset + corner, 30 - offset * 2, 30 - offset * 2 - corner * 2); // 横*/

			// ↑とかやってたけどこんなのがあったわ
			g.setColor(Color.black);
			g.fillOval(startDrawX + offset - thickness,
					startDrawY + offset - thickness,
					30 - offset * 2 + thickness * 2,
					30 - offset * 2 + thickness * 2);
			g.setColor(object.getColor());
			g.fillOval(startDrawX + offset, startDrawY + offset, 30 - offset * 2, 30 - offset * 2);
		} else {
			g.setColor(Color.black);
			g.fillRect(startDrawX, startDrawY, 30, 30);
		}
	}

	public static PuyoEntity getPuyoEntity() {
		return puyoEntity;
	}

	/**
	 *
	 * @return ランダムな色のPuyoEntityを返します
	 */

	public static PuyoEntity createPuyoEntity() {
		return new PuyoEntity(puyoColors[r.nextInt(puyoColors.length)], puyoColors[r.nextInt(puyoColors.length)]);
	}

	public static StageObject getStageObjectAt(int x, int y) {
		try {
			return objects[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

}
