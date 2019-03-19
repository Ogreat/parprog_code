package game.client.gui.elements;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

import game.client.gui.drawing.BoardDrawManager;
import game.server.GameServer;

import javax.swing.JPanel;

public class JBoardArea extends JPanel {

	private static final long serialVersionUID = -7855104718557647476L;
	public Options options = new Options();
	private BoardDrawManager dm;	
	
	public class Options {
		public static final int PREF_W = 600;
	   	public static final int PREF_H = 400;
	   	public static final int PREF_TICKNESS = 5;
	   	public static final float PREF_COEF = 0.8F;
	   	
	   	
	   	private boolean assistant = false;
	   	private boolean debug = false;
	   	
		public float getCoefSize() {
			return BoardDrawManager.coefSize;
		}
		public void setCoefSize(float coefSize) {
			BoardDrawManager.coefSize = coefSize;
			try { dm.sizedChanged(getWidth(), getHeight(), board.getWidht(), board.getHeight());
			} catch (RemoteException e) { e.printStackTrace(); }
			repaint();
		}
		public int getTickness() {
			return BoardDrawManager.tickness;
		}
		public void setTickness(int tickness) {
			BoardDrawManager.tickness = tickness;
			repaint();
		}
		public boolean isAssistant() {
			return assistant;
		}
		public void setAssistant(boolean assistant) {
			this.assistant = assistant;
			repaint();
		}
		public boolean isDebug() {
			return debug;
		}
		public void setDebug(boolean debug) {
			this.debug = debug;
			repaint();
		}
	}
	
   	private GameServer board;
	private Point current = null;
	private boolean hand = false;
	
	public JBoardArea(GameServer gameBoard) {
		setFocusable(true);
        this.board = gameBoard;
        try { this.dm = new BoardDrawManager(getWidth(), getHeight(), board.getWidht(), board.getHeight());
		} catch (RemoteException e2) { e2.printStackTrace(); }
        setEnabled(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	try { dm.sizedChanged(getWidth(), getHeight(), board.getWidht(), board.getHeight());
				} catch (RemoteException e1) { e1.printStackTrace(); }
        		repaint();
            }

         });
        addMouseMotionListener(new MouseAdapter() {
       	 @Override
       	 public void mouseMoved(MouseEvent e) {
       		 if (isEnabled() && dm.inArea(e.getX(), e.getY())) 
       		 {
       			 current = new Point(e.getX(), e.getY());
       			 Point cell = dm.getCell(e.getX(), e.getY());
       			 try {
					if (!board.getCellWall(cell.x, cell.y, dm.getSide(e.getX(), e.getY())).getState())
						if (dm.canClicked(e.getX(), e.getY()))
							{
								setCursor(new Cursor(Cursor.HAND_CURSOR));
								hand = true;
							}
					 	else 
					 		{
					 			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					 			hand = false;
					 		}
				} catch (RemoteException e1) { e1.printStackTrace(); }
       			 repaint();
       		 }
       		 else
       		 {
       			 current = null;
       			 repaint();
       		 }
       	 }
		});
        addMouseListener(new MouseAdapter() {
       	 @Override
       	 public void mouseClicked(MouseEvent e) {
       		 if(isEnabled() && dm.inArea(e.getX(), e.getY()) && hand)
       			{
       			Point cell = dm.getCell(e.getX(), e.getY());
       			 try {
					board.cellClick(cell.x, cell.y, dm.getSide(e.getX(), e.getY()));
				} catch (RemoteException e1) { e1.printStackTrace(); }
       			 repaint();
       			}
       	 }
		});
	
	}	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 		dm.drawClearBoard(g2d);
 		dm.drawBoardGrid(g2d, board);
 		if (this.options.debug) dm.drawDebug(g2d, board);
 		else { dm.drawBoardWalls(g2d, board);
 			dm.drawPlayers(g2d, board); }
 		if (this.options.assistant) dm.drawAssist(g2d, current, board);
 		if (current!=null && hand) dm.drawSelectedSide(g2d, current);
  }
	
  @Override
  	public Dimension getPreferredSize() {
       return new Dimension(Options.PREF_W, Options.PREF_H);
    }
	
	public void on() {
		setEnabled(true);
	}
	public void off() {
		setEnabled(false);
	}

	public void endGame() {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		setEnabled(false);
	}
}
