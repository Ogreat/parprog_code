package game.enigne;

import game.enigne.callbacks.BoardCallbacks;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class GameBoard implements Serializable{

	private static final long serialVersionUID = 925613405409691468L;
	public static class Options {
		public static final int MAX_PLAYERS_NUM = 4;
		public static final int SCORE_FOR_CELL = 5;
	}
	
	private BoardCallbacks callbacks = null;
	
	public void addCallbackListener(BoardCallbacks listener) {
		callbacks = listener;
	}
	
	private BoardCell[][] cells;
	private int width, height;
	
	private Player curPlayer;
	private Queue<Player> playerQueue;
	
	public int getWidht() { return width; }
	public int getHeight() { return height; }
	
	public GameBoard(int wCells,int hCells) {
		
		width = wCells; height = hCells;
		cells = new BoardCell[width][height];
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
				cells[i][j]=new BoardCell(i, j);

		for(int i=0;i<width-1;i++)
			for(int j=0;j<height;j++)
			{
				BoardWall wall = new BoardWall(false);
				try {
					cells[i][j].createWall(wall, Side.right);
					cells[i+1][j].createWall(wall, Side.left);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		for(int j=0;j<height-1;j++)
			for(int i=0;i<width;i++)
			{
				BoardWall wall = new BoardWall(false);
				try {
					cells[i][j].createWall(wall, Side.bottom);
					cells[i][j+1].createWall(wall, Side.top);
				} catch (Exception e) { e.printStackTrace(); }
			}

		try {
			for(int i=0;i<width;i++) cells[i][0].createWall(new BoardWall(true), Side.top);
			for(int i=0;i<width;i++) cells[i][height-1].createWall(new BoardWall(true), Side.bottom);
			for(int j=0;j<height;j++) cells[0][j].createWall(new BoardWall(true), Side.left);
			for(int j=0;j<height;j++) cells[width-1][j].createWall(new BoardWall(true), Side.right);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
				if (!cells[i][j].checkWalls()) assert(true);
		
		playerQueue = new LinkedList<Player>();
		curPlayer = playerQueue.poll();
				
	}

	public void newPlayer(Player p) {
		playerQueue.add(p);
	}
	
	public void startGame() {
		if (playerQueue.size() < 2) assert(true);
		curPlayer = playerQueue.poll();
		if (callbacks != null) callbacks.nextPlayer(curPlayer);
	}
	
	public Player getCellPlayer(int i, int j) {
		return cells[i][j].getPlayer();
	}
	public BoardWall getCellWall(int i, int j, Side side) {
		return cells[i][j].getWall(side);
	}
	public String getCellInfo(int i, int j) {
		return cells[i][j].getXYIndex();
	}
	public String getCellPlayerInfo(int i, int j) {
		return (String) (cells[i][j].getPlayer()==null?"---":cells[i][j].getPlayer().getName());
	}
	public Player getCurrentPlayer() {
		return curPlayer;
	}
	public void cellClick(int i, int j,Side side) {
		cells[i][j].getWall(side).setState(true);
		checkBoard();
		if (isEnd())
			if (callbacks != null) callbacks.gameOver(curPlayer);
		if (callbacks != null) callbacks.boardChanege();
	}

	private void checkBoard() {
		boolean wasSet = false;
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
				if (cells[i][j].checkStates() && cells[i][j].getPlayer()==null) 
					{
						try {
							cells[i][j].setPlayer(curPlayer);
						} catch (Exception e) { e.printStackTrace(); }
						curPlayer.addScore(Options.SCORE_FOR_CELL);
						callbacks.updateScore(curPlayer);
						wasSet = true;
					}
		if (!wasSet) 
			{
				nextPlayer();
				callbacks.nextPlayer(curPlayer);
			}
	}
	public boolean isEnd() {
		boolean yes = true;
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
				if (cells[i][j].getPlayer()==null) yes = false;
		return yes;
	}
	private void nextPlayer() {
		playerQueue.add(curPlayer);
		curPlayer = playerQueue.poll();
	}
	
}

