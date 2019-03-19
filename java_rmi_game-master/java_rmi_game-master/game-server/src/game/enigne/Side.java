package game.enigne;

import java.io.Serializable;

public enum Side implements Serializable {

	top,
	bottom,
	right,
	left;
	
	public static String toString(Side side) {
		switch (side) {
		case top: return "Top";
		case bottom: return "Bottom";
		case left: return "Left";
		case right: return "Right";
		default: return "WTF???";
		}
	}
}
