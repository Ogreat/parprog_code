package game.client.gui.elements;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import game.enigne.Player;
import java.awt.Dimension;

@SuppressWarnings("serial")
class JPlayerNote extends JPanel {
	
	private JLabel lblID;
	private JLabel lblName;
	private JLabel lblScore;
	private JLabel lblDP;
	private boolean current = false;
		
	public JPlayerNote(Player player) {
		setMaximumSize(new Dimension(230, 25));
		setMinimumSize(new Dimension(220, 20));
		
		setBorder(new LineBorder((current?Color.BLACK:Color.GRAY), 1, false));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("default:grow"),}));
		
		lblID = new JLabel("[" + player.getID() + "]");
		lblID.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblID.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblID, "2, 1");
		
		lblName = new JLabel(player.getName());
		lblName.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblName, "3, 1, default, center");
		
		lblDP = new JLabel(":");
		lblDP.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblDP.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblDP, "4, 1");
		
		lblScore = new JLabel(player.getScore() + "$");
		lblScore.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblScore, "5, 1");
		
		hide();
	}

	public void setScore(int newScore) {
		lblScore.setText(newScore+"$");
	}
	public void show() {
		setBorder(new LineBorder(Color.BLACK, 1, false));
			lblID.setEnabled(true);
			lblName.setEnabled(true);
			lblScore.setEnabled(true);
			lblDP.setEnabled(true);
	}
	public void hide() {
		setBorder(new LineBorder(Color.GRAY, 1, false));
			lblID.setEnabled(false);
			lblName.setEnabled(false);
			lblScore.setEnabled(false);
			lblDP.setEnabled(false);
	}
}