import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class SteeringPanel extends JPanel {

	private List<Renderable> renderables;
	private AffineTransform transform;
	private double fps;
	DecimalFormat format = new DecimalFormat("#.##");
	
	public SteeringPanel(AffineTransform transform) {
		renderables = new ArrayList<Renderable>();
		this.transform = transform;
	}
	
	public void addRenderable(Renderable r) {
		renderables.add(r);
	}
	
	public void setFPS(double fps) {
		this.fps = fps;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldTransform = g2.getTransform();
		g2.setTransform(transform);
		for (Renderable renderable : renderables) {
			renderable.draw(g);
		}
		g2.setTransform(oldTransform);
		g.drawString(String.valueOf((renderables.size() - 10)), 10, 10);
		g.drawString(format.format(fps), 50, 10);
	}
	
}
