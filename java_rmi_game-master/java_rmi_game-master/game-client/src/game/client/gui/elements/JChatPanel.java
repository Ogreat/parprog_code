package game.client.gui.elements;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class JChatPanel extends JPanel {
	private JTextField textField;
	private JTextPane textPane;
	private JButton btnNewButton;

	private void appendString(String text, SimpleAttributeSet keyWord) {
		StyledDocument doc = textPane.getStyledDocument();
		try { doc.insertString(doc.getLength(), text, keyWord);
		} catch(Exception e) { e.printStackTrace(); }
	}
	
	public void newMessage(String name, String text) {
		
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setBold(keyWord, true);
		appendString("\n" + name + ": ", keyWord);
		appendString(text, null);
		
	}
	
	public JChatPanel() {
		setMinimumSize(new Dimension(205, 150));
		
		setLayout(new BorderLayout(0, 0));
		textPane = new JTextPane();
		textPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textPane);
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		btnNewButton = new JButton("");
		
		btnNewButton.setIcon(new ImageIcon(JChatPanel.class.getResource("/res/icons/bullet_go_2777.png")));
		panel.add(btnNewButton, BorderLayout.EAST);
		
		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		textPane.setText("Chat started at " + timeStamp);
	}
	public void addNewMessageListener(ActionListener listener) {
		btnNewButton.addActionListener(listener);
	}
	public String getTextMessage() {
		return textField.getText();
	}
}