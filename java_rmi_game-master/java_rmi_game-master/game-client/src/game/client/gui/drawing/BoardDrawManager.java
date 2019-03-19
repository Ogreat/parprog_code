package game.client.gui.drawing;

import game.client.gui.elements.JBoardArea;
import game.enigne.Player;
import game.enigne.Side;
import game.server.GameServer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.rmi.RemoteException;

public final class BoardDrawManager {

	private static float offsetX, offsetY;
	private static float cellSize;
	private static float width, height;
	public static int tickness;	
	public static float coefSize;

	public Side getSideF(float X, float Y) {
  		if (Y>X)
  			if (cellSize-X>Y) return Side.left;
  			else return Side.bottom;
  		else
  			if (cellSize-X>Y) return Side.top;
  			else return Side.right;
  		
  	}
	public Side getSide(int x, int y) {
		Point cell = getCell(x, y);
		Side side = getSideF(x - cell.x*cellSize - offsetX, y - cell.y*cellSize - offsetY);
  		return side;		
  	} 
	public void drawLine(int i,int j, Side s, Graphics2D g2d) {
 		switch(s) {
  		case bottom:
  			g2d.drawLine((int)(offsetX+i*cellSize), (int)(offsetY+(j+1)*cellSize), 
  					(int)(offsetX+(i+1)*cellSize), (int)(offsetY+(j+1)*cellSize));
  			break;
  		case left:
  			g2d.drawLine((int)(offsetX+i*cellSize), (int)(offsetY+j*cellSize), 
  					(int)(offsetX+i*cellSize), (int)(offsetY+(j+1)*cellSize));
  			break;
  		case right:
  			g2d.drawLine((int)(offsetX+(i+1)*cellSize), (int)(offsetY+j*cellSize), 
  					(int)(offsetX+(i+1)*cellSize), (int)(offsetY+(j+1)*cellSize));
  			break;
  		case top:
  			g2d.drawLine((int)(offsetX+i*cellSize), (int)(offsetY+j*cellSize), 
  					(int)(offsetX+(i+1)*cellSize), (int)(offsetY+j*cellSize));
  			break;
  		}
    }
	public Point getCell(int x, int y) {
		return new Point((int)(Math.floor((x-offsetX))/cellSize),(int)(Math.floor((y-offsetY))/cellSize));
	}

	public BoardDrawManager(float width, float height, int board_w, int board_h) {
		BoardDrawManager.width = width; BoardDrawManager.height = height;
		BoardDrawManager.tickness = JBoardArea.Options.PREF_TICKNESS;
		BoardDrawManager.coefSize = JBoardArea.Options.PREF_COEF;
		cellSize = Math.min((float)(width*coefSize/board_w), (float)(height*coefSize/board_h));
		offsetX = (width - board_w*cellSize)/2; 	
		offsetY = (height - board_h*cellSize)/2;		
	}
	public void sizedChanged(float width, float height, int board_w, int board_h) {
		BoardDrawManager.width = width; BoardDrawManager.height = height;
		cellSize = Math.min((float)(width*coefSize/board_w), (float)(height*coefSize/board_h));
		offsetX = (width - board_w*cellSize)/2;
		offsetY = (height - board_h*cellSize)/2;
	}
	public boolean inArea(int x, int y) {
  		if (x>offsetX && x<width-offsetX && y>offsetY && y<height-offsetY) return true;
  		else return false;
  	}
	
	public void drawClearBoard(Graphics2D g2d) {
		g2d.setColor(Color.WHITE); 
 		g2d.fillRect((int)(offsetX), (int)(offsetY), (int)(width- 2*offsetX), (int)(height- 2*offsetY));
	}
	public void drawBoardGrid(Graphics2D g2d, GameServer remoteServer) {
		g2d.setColor(Color.BLACK);
 		g2d.setStroke(new BasicStroke(1.0F));
 		try {
			for(int i=0;i<= remoteServer.getWidht();i++)
				g2d.drawLine((int)(offsetX+i*cellSize), (int)(offsetY), 
						(int)(offsetX+i*cellSize), (int)(height-offsetY-1));
			for(int j=0;j<=remoteServer.getHeight();j++) 
				g2d.drawLine((int)(offsetX), (int)(offsetY+j*cellSize), 
						(int)(width-offsetX-1), (int)(offsetY+j*cellSize));
		} catch (RemoteException e) { e.printStackTrace(); }
	}
	public void drawPlayers(Graphics2D g2d, GameServer remoteServer) {
		try {
			for(int i=0;i<remoteServer.getWidht();i++)
				for(int j=0;j<remoteServer.getHeight();j++)
				{
					Player p = remoteServer.getCellPlayer(i, j);
					if (p!=null)
						DrawFigure.draw(g2d, p.getFigure(), p.getColor(), 
								(int)(offsetX + i*cellSize), (int)(offsetY + j*cellSize) , (int)(cellSize));
				}
		} catch (RemoteException e) { e.printStackTrace(); }
	}
	public void drawBoardWalls(Graphics2D g2d, GameServer remoteServer) {
		g2d.setStroke(new BasicStroke(tickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
  	  	try {
			for(int i=0;i<remoteServer.getWidht();i++)
				for(int j=0;j<remoteServer.getHeight();j++)
				{
					if (remoteServer.getCellWall(i, j, Side.top).getState()) drawLine(i, j, Side.top, g2d); 
					if (remoteServer.getCellWall(i, j, Side.bottom).getState()) drawLine(i, j, Side.bottom, g2d); 
					if (remoteServer.getCellWall(i, j, Side.left).getState()) drawLine(i, j, Side.left, g2d); 
					if (remoteServer.getCellWall(i, j, Side.right).getState()) drawLine(i, j, Side.right, g2d); 
				}
		} catch (RemoteException e) { e.printStackTrace(); }
	}
	public void drawAssist(Graphics2D g2d, Point point, GameServer remoteServer) {
		if (point!=null) {
			Point cell = getCell(point.x, point.y);
			g2d.setColor(new Color(0.95F,0.95F,0.95F,0.5F));
			g2d.fillRoundRect((int)(offsetX+cell.x*cellSize+cellSize*0.1) , (int)(offsetY+cell.y*cellSize+cellSize*0.1), 
				(int)(cellSize*0.8), (int)(cellSize*0.8), (int)(cellSize*0.3), (int)(cellSize*0.3));
			g2d.setColor(Color.LIGHT_GRAY);
			Side side = getSideF(point.x - cell.x*cellSize - offsetX, point.y - cell.y*cellSize - offsetY);
			g2d.drawString(side!=null ? Side.toString(side) : "---", 
					(int)(offsetX+cell.x*cellSize+cellSize*0.2), (int)(offsetY+cell.y*cellSize+cellSize*0.4));
			try {
			g2d.drawString(remoteServer.getCellInfo(cell.x, cell.y), 
					(int)(offsetX+cell.x*cellSize+cellSize*0.2), (int)(offsetY+cell.y*cellSize+cellSize*0.6));
			g2d.drawString(remoteServer.getCellPlayerInfo(cell.x, cell.y), 
					(int)(offsetX+cell.x*cellSize+cellSize*0.2), (int)(offsetY+cell.y*cellSize+cellSize*0.8));
		} catch (RemoteException e) { e.printStackTrace(); }
		}
	}
	public void drawSelectedSide(Graphics2D g2d, Point point) {
		g2d.setColor(Color.BLACK);
 		g2d.setStroke(new BasicStroke(tickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
 		Point cell = getCell(point.x, point.y);
 		Side side = getSideF(point.x - cell.x*cellSize - offsetX, point.y - cell.y*cellSize - offsetY);
 		drawLine(cell.x, cell.y, side, g2d);
	}
	public void drawDebug(Graphics2D g2d, GameServer remoteServer) {
		try {
			for(int i=0;i<remoteServer.getWidht();i++)
				for(int j=0;j<remoteServer.getHeight();j++)
					{
						g2d.drawString(remoteServer.getCellInfo(i, j), 
								(int)(offsetX+i*cellSize)+2, (int)(offsetY+(j+1)*cellSize)-3);
						g2d.drawString(remoteServer.getCellPlayerInfo(i, j), 
						(int)(offsetX+i*cellSize+0.2*cellSize), (int)(offsetY+j*cellSize+0.4*cellSize));
					}
		} catch (RemoteException e) { e.printStackTrace(); }
		try {
			for(int i=0;i<remoteServer.getWidht();i++)
				for(int j=0;j<remoteServer.getHeight();j++)
				{
					float x = offsetX+i*cellSize;
					float y = offsetY+j*cellSize;
					g2d.drawString(remoteServer.getCellWall(i, j, Side.top).getState()?"--":"", x+cellSize/2, y+10);
					g2d.drawString(remoteServer.getCellWall(i, j, Side.left).getState()?"|":"", x+5, y+cellSize/2);
					g2d.drawString(remoteServer.getCellWall(i, j, Side.bottom).getState()?"--":"", x+cellSize/2, y+cellSize-6);
					g2d.drawString(remoteServer.getCellWall(i, j, Side.right).getState()?"|":"", x+cellSize-10, y+cellSize/2);
				}
		} catch (RemoteException e) { e.printStackTrace(); }
	}
	public boolean canClicked(int x, int y) {
		Point cell = getCell(x, y);
		float X = x - cell.x*cellSize - offsetX;
		float Y = y - cell.y*cellSize - offsetY;
		if (X<cellSize*0.1 || X>cellSize*0.9 || Y<cellSize*0.1 || Y>cellSize*0.9) return true;
    	else return false;
	}
	
	
	
}
