package game.client.gui.elements;

import game.enigne.Player;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class JEndDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	public JEndDialog(int playerID, List<Player> list) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(JEndDialog.class.getResource("/res/icons/page_white_medal_4193.png")));
		setTitle("Game Over");
		setResizable(false);
		
		int place = -1;
		for(int i=0;i<list.size();i++)
			if (list.get(i).getID() == playerID)
			{
				place = i;
				break;
			}
			
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 257, 260);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScoreTable panel = new JScoreTable(list);
			panel.nextPlayer(playerID);
			contentPanel.add(panel, BorderLayout.CENTER);
		}
		{
			JLabel lblNewLabel = new JLabel("You are in " + (place + 1) + " place");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			switch (place) {
			case 0: 
				lblNewLabel.setIcon(new ImageIcon(JEndDialog.class.getResource("/res/icons/medal_gold_1_4112.png")));
				break;
			case 1: 
				lblNewLabel.setIcon(new ImageIcon(JEndDialog.class.getResource("/res/icons/medal_silver_3_3507.png")));
				break;
			case 2: 
				lblNewLabel.setIcon(new ImageIcon(JEndDialog.class.getResource("/res/icons/medal_bronze_3_4825.png")));
				break;
			default:
				lblNewLabel.setIcon(new ImageIcon(JEndDialog.class.getResource("/res/icons/user_5290.png")));
				break;
			}
			
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
			contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
