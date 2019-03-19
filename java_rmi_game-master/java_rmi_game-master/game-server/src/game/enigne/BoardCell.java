package game.enigne;

import java.io.Serializable;

public class BoardCell implements Serializable {

	private static final long serialVersionUID = -4316623430695585413L;
	private int xIndex;
	private int yIndex;
	private Player player;
	private BoardWall[] walls = new BoardWall[4]; 
	
	public BoardCell(int xIndex, int yIndex) {
		this.xIndex = xIndex;
		this.yIndex = yIndex;
	}
	private static int index(Side side) {
		switch(side) {
		case bottom: return 2;
		case left: return 3;
		case right: return 1;
		case top: return 0;
		default: return -1;
		}
	}
	
	public void createWall(BoardWall wall, Side side) throws Exception {
		if(walls[index(side)] == null) walls[index(side)] = wall; 
		else throw new Exception("wall["+index(side)+"] in ["+xIndex+","+yIndex+"] is'n null");
	}
	public boolean checkWalls() {
		if (walls[0]!= null && walls[1]!=null && walls[2]!=null && walls[3]!=null) return true;
		else return false;
	}
	public String getXYIndex() {
		return "["+xIndex+";"+yIndex+"]";
	}
	public BoardWall getWall(Side side) {
		return walls[index(side)];
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) throws Exception {
		if (this.player==null) this.player = player;
		else throw new Exception("player in ["+xIndex+","+yIndex+"] is'n null");
	}
	public boolean checkStates() {
		if (walls[0].getState() && walls[1].getState() && walls[2].getState() && walls[3].getState())
			return true;
		else return false;
	}
	
}