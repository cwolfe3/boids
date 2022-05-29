import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class BoidMouseListener implements MouseListener, MouseMotionListener {
	
	private List<Boid> boids = new ArrayList<Boid>();
	private Thread mouseInputThread;
	private Vector mousePos;
	private boolean mouseDown;
	private AffineTransform transform;
	
	public BoidMouseListener(AffineTransform transform) {
		mousePos = new Vector(0, 0);
		this.transform = transform;
	}
	
	public void start() {
		mouseInputThread = new Thread() {
			public void run() {
				while (mouseDown) {
					for (Boid boid : boids) {
						boid.attract(mousePos, 500000);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		mouseInputThread.start();
	}
	
	public void addBoid(Boid boid) {
		boids.add(boid);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseDown = true;
		Point2D mousePoint = e.getPoint();
		mousePoint = transform.transform(mousePoint, null);
		mousePos = new Vector(mousePoint.getX(), mousePoint.getY());
		start();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		mouseDown = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D mousePoint = e.getPoint();
		mousePoint = transform.transform(mousePoint, null);
		mousePos = new Vector(mousePoint.getX(), mousePoint.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point2D mousePoint = e.getPoint();
		mousePoint = transform.transform(mousePoint, null);
		mousePos = new Vector(mousePoint.getX(), mousePoint.getY());
	}
}
