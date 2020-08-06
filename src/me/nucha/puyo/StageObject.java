package me.nucha.puyo;

import java.awt.Color;

public class StageObject {

	private Color color;

	public StageObject(Color color) {
		this.color = color;
	}

	public StageObject() {
	}

	public Color getColor() {
		return color;
	}

	public boolean isPuyo() {
		return color != null;
	}

}
