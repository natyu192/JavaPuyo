package me.nucha.puyo;

import java.awt.Color;

public class PuyoEntity {

	private int x;
	private int y;
	private StageObject puyoMain;
	private StageObject puyoSub;
	private int subDirection;
	private boolean spawned;
	// 0↑ 1← 2↓ 3→
	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;

	public PuyoEntity(Color colorMain, Color colorSub) {
		this.spawned = false;
		this.puyoMain = new StageObject(colorMain);
		this.puyoSub = new StageObject(colorSub);
		this.subDirection = UP;
	}

	public void spawn() {
		if (spawned) {
			return;
		}
		spawned = true;
		this.x = PuyoConfig.START_X;
		this.y = PuyoConfig.START_Y;
	}

	/**
	 * 操作中のぷよを左右どちらかに移動します
	 *
	 * @param direction PuyoEntity#LEFT か PuyoEntity#RIGHT のみを設定します
	 * @return 正しく移動できた場合はtrue、何らかの理由で移動できなかった場合はfalseを返します
	 */

	public boolean move(int direction) {
		switch (direction) {
		case LEFT:
			if (PuyoStage.getStageObjectAt(x - 1, y) != null ||
					PuyoStage.getStageObjectAt(getSubX() - 1, getSubY()) != null) { // メインかサブぷよの左にぷよか壁がある場合
				return false;
			}
			x--;
			return true;
		case RIGHT:
			if (PuyoStage.getStageObjectAt(x + 1, y) != null ||
					PuyoStage.getStageObjectAt(getSubX() + 1, getSubY()) != null) { // メインかサブぷよの右にぷよか壁がある場合
				return false;
			}
			x++;
			return true;
		default:
			return false;
		}
	}

	/**
	 * 操作中のぷよにくっついているぷよを回転（移動）させます
	 *
	 * @param direction PuyoEntity#LEFT か PuyoEntity#RIGHT のみを設定します
	 * @return 正しく回転できた場合はtrue、何らかの理由で回転できなかった場合はfalseを返します
	 */

	public boolean rotate(int direction) {
		switch (direction) {
		case LEFT:
			switch (subDirection) { // 現在の向き
			case UP:// 上向き
				if ((PuyoStage.getStageObjectAt(x - 1, y) != null ||
						PuyoStage.getStageObjectAt(getSubX() - 1, getSubY()) != null) && !move(RIGHT)) {// 左にぷよか壁があり、右にひとつ動けない場合
					return false;
				}
				subDirection = LEFT;
				return true;
			case DOWN: // 下向き
				if ((PuyoStage.getStageObjectAt(x + 1, y) != null ||
						PuyoStage.getStageObjectAt(getSubX() + 1, getSubY()) != null) && !move(LEFT)) {// 右にぷよか壁があり、左にひとつ動けない場合
					return false;
				}
				subDirection = RIGHT;
				return true;
			case LEFT: // 左向き
				if (PuyoStage.getStageObjectAt(x, y + 1) != null) { // 下にぷよか壁がある場合
					y--; // ひとつ上に移動する
				}
				subDirection = DOWN;
				return true;
			case RIGHT:
				subDirection = UP;
				return true;
			}
		case RIGHT:
			switch (subDirection) { // 現在の向き
			case UP:// 上向き
				if ((PuyoStage.getStageObjectAt(x + 1, y) != null ||
						PuyoStage.getStageObjectAt(getSubX() + 1, getSubY()) != null) && !move(LEFT)) {// 右にぷよか壁があり、左にひとつ動けない場合
					return false;
				}
				subDirection = RIGHT;
				return true;
			case DOWN: // 下向き
				if ((PuyoStage.getStageObjectAt(x - 1, y) != null ||
						PuyoStage.getStageObjectAt(getSubX() - 1, getSubY()) != null) && !move(RIGHT)) {// 左にぷよか壁があり、右にひとつ動けない場合
					return false;
				}
				subDirection = LEFT;
				return true;
			case LEFT:// 左向き
				subDirection = UP;
				return true;
			case RIGHT: // 右向き
				if (PuyoStage.getStageObjectAt(x, y + 1) != null) { // 下にぷよか壁がある場合
					y--; // ひとつ上に移動する
				}
				subDirection = DOWN;
				return true;
			}
			return false;
		default:
			return false;
		}
	}

	/**
	 * 操作中のぷよを下に１マス移動させます
	 *
	 * @return 正しく移動できた場合はtrue、何らかの理由で移動できなかった場合はfalseを返します
	 */

	public boolean fall() {
		if (PuyoStage.getStageObjectAt(x, y + 1) != null
				|| PuyoStage.getStageObjectAt(getSubX(), getSubY() + 1) != null) {
			return false;
		}
		y++;
		return true;
	}

	public int getMainX() {
		return x;
	}

	public int getMainY() {
		return y;
	}

	public int getSubX() {
		switch (subDirection) {
		case LEFT:
			return x - 1;
		case RIGHT:
			return x + 1;
		default:
			return x;
		}
	}

	public int getSubY() {
		switch (subDirection) {
		case UP:
			return y - 1;
		case DOWN:
			return y + 1;
		default:
			return y;
		}
	}

	public StageObject getPuyoMain() {
		return puyoMain;
	}

	public StageObject getPuyoSub() {
		return puyoSub;
	}

}
