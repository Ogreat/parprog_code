package game.enigne;

import java.util.Comparator;

public class PlayerComp implements Comparator<Player> {
	@Override
	public int compare(Player a, Player b) {
		if( a.getScore()> b.getScore()) return -1; 
	    if(a.getScore() == b.getScore()) return 0;
	    return 1;
	}	
}
