package game.client.gui.drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public final class DrawFigure {
	protected static void draw_1(Graphics2D g2d,float x, float y, float cell_size) {
    	g2d.drawLine((int)(x+cell_size*0.3), (int)(y+cell_size*0.7), (int)(x+cell_size*0.7), (int)(y+cell_size*0.3));
    	g2d.drawLine((int)(x+cell_size*0.3), (int)(y+cell_size*0.3), (int)(x+cell_size*0.7), (int)(y+cell_size*0.7));
	}
	protected static void draw_2(Graphics2D g2d,int x, int y, int cell_size) {
    	g2d.drawOval((int)(x+cell_size*0.3), (int)(y+cell_size*0.3), (int)(cell_size*0.4), (int)(cell_size*0.4));
	}
	protected static void draw_3(Graphics2D g2d,int x, int y, int cell_size) {
		int xP[] = {(int)(x+cell_size*0.5),(int)(x+cell_size*0.7),(int)(x+cell_size*0.3)};
    	int yP[] = {(int)(y+cell_size*0.3),(int)(y+cell_size*0.7),(int)(y+cell_size*0.7)};
    	g2d.drawPolygon(xP, yP, 3);
	}
	protected static void draw_4(Graphics2D g2d,int x, int y, int cell_size) {
		g2d.drawRect((int)(x+cell_size*0.3), (int)(y+cell_size*0.3), (int)(cell_size*0.4), (int)(cell_size*0.4));
	}
	protected static void draw_def(Graphics2D g2d,int x, int y, int cell_size) { }
	public static void draw(Graphics2D g2d, int figure, Color color,int x, int y, int cell_size) {
		g2d.setStroke(new BasicStroke(2*BoardDrawManager.tickness, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
    	g2d.setColor(color);
    	switch (figure) {
		case 1: draw_1(g2d, x, y, cell_size); break;
		case 2: draw_2(g2d, x, y, cell_size); break;
		case 3: draw_3(g2d, x, y, cell_size); break;
		case 4: draw_4(g2d, x, y, cell_size); break;
		default: draw_def(g2d, x, y, cell_size); break;
		}
	}
}
