package game.enigne;

import java.io.Serializable;

public class BoardWall implements Serializable {

	private static final long serialVersionUID = 7191170178194742899L;
	private boolean state = false;
	
	public BoardWall() {}
	public BoardWall(boolean state) {
		this.state = state;
	}
	
	public boolean getState(){
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}

}