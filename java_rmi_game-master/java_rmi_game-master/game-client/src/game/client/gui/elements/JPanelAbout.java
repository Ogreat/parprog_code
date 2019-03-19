package game.client.gui.elements;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class JPanelAbout extends JPanel {

	private JTextPane textPane;
	
	public void setText(String text) {
		textPane.setText(text);
	}
	public JPanelAbout() {
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("\u0418\u0433\u0440\u0430 \"\u041A\u043E\u0440\u0438\u0434\u043E\u0440\u0447\u0438\u043A\u0438\"");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		add(lblNewLabel, BorderLayout.NORTH);
		
		JLabel lblNewLabel_1 = new JLabel("Author Aratsky Alexandr ");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblNewLabel_1, BorderLayout.SOUTH);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		
		textPane.setBackground(UIManager.getColor("Button.background"));
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 11));
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setMaximumSize(new Dimension(205, 32767));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

	}

}
