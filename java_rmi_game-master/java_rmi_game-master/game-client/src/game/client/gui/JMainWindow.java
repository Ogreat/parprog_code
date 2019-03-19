package game.client.gui;

import game.client.gui.elements.JBoardArea;
import game.client.gui.elements.JChatPanel;
import game.client.gui.elements.JEndDialog;
import game.client.gui.elements.JOptionsPanel;
import game.client.gui.elements.JPanelAbout;
import game.client.gui.elements.JScoreTable;
import game.enigne.Player;
import game.server.GameServer;
import game.server.impl.ServerCallbacksAdapter;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class JMainWindow extends JFrame {

	private JPanel contentPane;
	private GameServer server;
	private static int playerID = -1;
	private static String playerName;
	
	private JBoardArea gameArea;
	private JScoreTable scoreArea;
	private JChatPanel chat;
	private JOptionsPanel optons;
	private JPanel scoreBoard;

	/**
	 * Create the frame.
	 */
	public JMainWindow(GameServer gameServer, String name, int figure, String colorS) {
		setTitle("Goridors 1.0: " + name);
		this.server = gameServer;
		
		try {
			server.addCallbackListener(new ServerCallbacksAdapter() {
				private static final long serialVersionUID = -956862938989184585L;
				@Override
				public void boardChanege() throws RemoteException {
					gameArea.repaint();
				}
				@Override
				public void nextPlayer(Player next) throws RemoteException {
					scoreArea.nextPlayer(next.getID());
					if (next.getID() == playerID) gameArea.on();
					else gameArea.off();
				}
				@Override
				public void updateScore(int ID, int score) throws RemoteException {
					scoreArea.upgradeScore(ID, score);
				}
				@Override
				public void startGame() throws RemoteException {
					scoreArea = new JScoreTable(server.getPlayerslist());
					scoreBoard.add(scoreArea, BorderLayout.CENTER);
					scoreArea.setVisible(true);
					gameArea.setVisible(true);
					chat.setVisible(true);
					optons.setVisible(true);
				}
				@Override
				public void newMessageForChat(String name, String text) throws RemoteException {
					chat.newMessage(name, text);
				}
				@Override
				public void gameOver(int idWinner) throws RemoteException {
					gameArea.endGame();
					scoreArea.setEnabled(false);
					optons.setEnabled(false);
					chat.setEnabled(false);
					JEndDialog dialog = new JEndDialog(playerID, server.getPlayerslist());
					dialog.setBounds(getBounds().x, getBounds().x, 260, 260);
					dialog.setVisible(true);
				}
			});
			playerID = server.addPlayer(name, figure, colorS);
			if (playerID == -1) {
				JOptionPane.showMessageDialog(this, "Game already started!", "Game started", JOptionPane.CLOSED_OPTION);
				System.exit(-1);
			}
        	playerName = name;
		} catch (RemoteException e) { e.printStackTrace(); }	
		
		initGUI(server);
	}
	
	private void initGUI(GameServer board) {
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(JMainWindow.class.getResource("/res/icons/package_games_board_9247.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(100, 100, 700, 500);
		contentPane =  new JPanel();
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(100dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		setContentPane(contentPane);
		
		JPanel gameBoard = new JPanel();
		TitledBorder title1 = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Game Board");
		title1.setTitleJustification(TitledBorder.LEFT);
		gameBoard.setBorder(title1);
		gameBoard.setLayout(new BorderLayout(0, 0));
		contentPane.add(gameBoard, "2, 2, 2, 3, fill, fill");
		
		gameArea = new JBoardArea(board);
		gameArea.setVisible(false);
		gameBoard.add(gameArea, BorderLayout.CENTER);
		
		
		scoreBoard = new JPanel();
		scoreBoard.setLayout(new BorderLayout(0, 0));
		TitledBorder title = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Score Table");
		title.setTitleJustification(TitledBorder.CENTER);
		scoreBoard.setBorder(title);
		contentPane.add(scoreBoard, "5, 2, fill, fill");
		
		final JPanel optionsPanel = new JPanel();
		contentPane.add(optionsPanel, "5, 4, fill, fill");
		optionsPanel.setLayout(new BorderLayout(0, 0));
		TitledBorder title3 = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Options");
		title3.setTitleJustification(TitledBorder.LEFT);
		optionsPanel.setBorder(title3);
				
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		optionsPanel.add(tabbedPane);
		
		JLayeredPane optionsGraphics = new JLayeredPane();
		tabbedPane.addTab("Graphics", null, optionsGraphics, null);
		optionsGraphics.setLayout(new BorderLayout(0, 0));
		
		optons = new JOptionsPanel(gameArea);
		optons.setVisible(false);
		optionsGraphics.add(optons, BorderLayout.CENTER);
		
		JLayeredPane connectionPanel = new JLayeredPane();
		tabbedPane.addTab("Chat", null, connectionPanel, null);
		connectionPanel.setLayout(new BorderLayout(0, 0));
		chat = new JChatPanel();
		chat.setVisible(false);
		chat.addNewMessageListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					server.newMessage("[" + playerID + "] " + playerName, chat.getTextMessage());
				} catch (RemoteException e1) { e1.printStackTrace(); }
			}
		});
		connectionPanel.add(chat, BorderLayout.CENTER);
		
		JLayeredPane aboutPanel = new JLayeredPane();
		tabbedPane.addTab("About", null, aboutPanel, null);
		aboutPanel.setLayout(new BorderLayout(0, 0));
		
		JPanelAbout about = new JPanelAbout();
		about.setMaximumSize(new Dimension(205, 2147483647));
		aboutPanel.add(about, BorderLayout.CENTER);
		about.setText("\u0412 \u0445\u043E\u0434\u0435 \u0438\u0433\u0440\u044B \u0438\u0433\u0440\u0430\u044E\u0449\u0438\u0435 \u043F\u043E\u043E\u0447\u0435\u0440\u0435\u0434\u043D\u043E \r\n\u043F\u0440\u043E\u0432\u043E\u0434\u044F\u0442 \u0432\u0435\u0440\u0442\u0438\u043A\u0430\u043B\u044C\u043D\u044B\u0435 \u0438\u043B\u0438 \r\n\u0433\u043E\u0440\u0438\u0437\u043E\u043D\u0442\u0430\u043B\u044C\u043D\u044B\u0435 \u043B\u0438\u043D\u0438\u0438 \u0434\u043B\u0438\u043D\u043E\u0439 \u0432 \r\n\u043E\u0434\u043D\u0443 \u043A\u043B\u0435\u0442\u043A\u0443. \u0417\u0430\u0434\u0430\u0447\u0430 \u2013 \u0437\u0430\u043C\u043A\u043D\u0443\u0442\u044C \r\n\u0441\u0432\u043E\u0435\u0439 \u043B\u0438\u043D\u0438\u0435\u0439 \u043A\u043B\u0435\u0442\u043A\u0443. \u0418\u0433\u0440\u043E\u043A, \r\n\u043A\u043E\u0442\u043E\u0440\u043E\u043C\u0443 \u044D\u0442\u043E \u0443\u0434\u0430\u043B\u043E\u0441\u044C, \u0441\u0442\u0430\u0432\u0438\u0442 \r\n\u0432 \u043A\u043B\u0435\u0442\u043A\u0435 \u0441\u0432\u043E\u0439 \u0443\u0441\u043B\u043E\u0432\u043D\u044B\u0439 \u0437\u043D\u0430\u043A. \r\n\u0412\u0434\u043E\u0431\u0430\u0432\u043E\u043A, \u0442\u0430\u043A\u043E\u0439 \u0438\u0433\u0440\u043E\u043A \r\n\u043F\u043E\u043B\u0443\u0447\u0430\u0435\u0442 \u0434\u043E\u043F\u043E\u043B\u043D\u0438\u0442\u0435\u043B\u044C\u043D\u044B\u0439 \u0445\u043E\u0434.\r\n\u0418\u0433\u0440\u0430 \u0438\u0434\u0435\u0442 \u0434\u043E \u0442\u0435\u0445 \u043F\u043E\u0440, \u043F\u043E\u043A\u0430 \u043D\u0435 \r\n\u0431\u0443\u0434\u0435\u0442 \u0437\u0430\u043F\u043E\u043B\u043D\u0435\u043D\u043E \u0432\u0441\u0435 \u043F\u043E\u043B\u0435. \r\n\u041F\u043E\u0441\u043B\u0435 \u044D\u0442\u043E\u0433\u043E \u043F\u043E\u0434\u0441\u0447\u0438\u0442\u044B\u0432\u0430\u044E\u0442, \r\n\u00AB\u0447\u044C\u0438\u0445\u00BB \u043A\u043B\u0435\u0442\u043E\u043A \u043D\u0430 \u043F\u043E\u043B\u0435 \u0431\u043E\u043B\u044C\u0448\u0435. \r\n\u042D\u0442\u043E\u0442 \u0438\u0433\u0440\u043E\u043A \u0438 \u0441\u0442\u0430\u043D\u043E\u0432\u0438\u0442\u0441\u044F \r\n\u043F\u043E\u0431\u0435\u0434\u0438\u0442\u0435\u043B\u0435\u043C.");
		
	}

}
