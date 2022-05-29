import java.awt.Graphics;


public class CircleObstacle implements Renderable {

	private Vector center;
	private double radius;
	
	public CircleObstacle(Vector center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Vector getPosition() {
		return center;
	}
	
	public double getRadius() {
		return radius;
	}

	@Override
	public void draw(Graphics g) {
		g.fillOval((int)(center.getElem(0) - radius), (int)(center.getElem(1) - radius), (int)(2 * radius), (int)(2 * radius));
	}
	
}
