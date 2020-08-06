package me.nucha.puyo;

public class PuyoConfig {

	public static final int STAGE_WIDTH = 8;
	public static final int STAGE_HEIGHT = 14;

	public static final int WIDTH = (PuyoConfig.STAGE_WIDTH * 30) + 14 + 120;
	public static final int HEIGHT = (PuyoConfig.STAGE_HEIGHT * 30) + 34 - 34;

	public static final int START_X = 3;
	public static final int START_Y = 1;

	public static final int NEXT_X = 9;
	public static final int NEXT_Y = 2;

	public static final int INTERVAL_NATURAL_FALL = 50;
	public static final int INTERVAL_FALL = 10;
	public static final int MAX_FAIL_TO_FALL = 1;
	public static final int MIN_PUYOS_TO_REMOVE = 5;

}
