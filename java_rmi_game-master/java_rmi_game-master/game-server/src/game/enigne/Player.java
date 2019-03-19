package game.enigne;

import java.awt.Color;
import java.io.Serializable;

public class Player implements Serializable {

	private static final long serialVersionUID = 797114087962339063L;
	private static int playerCount = 0;
	private int id;
	private String name;
	private int figure;
	private Color color;
	private int score = 0;

	public Player(String name, int figure, Color color) {
		playerCount++;
		if (playerCount>GameBoard.Options.MAX_PLAYERS_NUM) assert(true);
		id = playerCount;
		this.name = name;
		this.figure = figure;
		this.color = color;
	}
	public int getScore() {
		return score;
	}
	public void addScore(int points) {
		score+=points;
	}
	public void clearScore() {
		score = 0;
	}
	public int getID() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getFigure() {
		return figure;
	}
	public Color getColor() {
		return color;
	}

}
