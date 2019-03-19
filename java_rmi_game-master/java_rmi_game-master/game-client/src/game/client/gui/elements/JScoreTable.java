package game.client.gui.elements;

import game.enigne.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.BoxLayout;

@SuppressWarnings("serial")
public class JScoreTable extends JPanel {
	
	private Map<Integer, JPlayerNote> playersMap;
	
	public JScoreTable(List<Player> players) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		playersMap = new HashMap<Integer,JPlayerNote>();
		for(Player p : players) {
			JPlayerNote tmp = new JPlayerNote(p);
			add(tmp);
			playersMap.put(p.getID(), tmp);
		}
	}
	
	public void upgradeScore(int id, int score) {
		playersMap.get(id).setScore(score);
	}
	
	public void nextPlayer(int nextID) {
		for(int key : playersMap.keySet()) 
			if (key == nextID) playersMap.get(key).show();
			else playersMap.get(key).hide();
	}
}
