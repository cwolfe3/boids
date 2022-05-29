import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Main {
	
	private JFrame window;
	private SteeringPanel panel;
	private BoidMouseListener listener;
	private CollisionPreventer preventer;
	private boolean running;
	private int numBoidsToCreate;
	
	private List<Boid> boids;
	private List<Long> timeDelays = new LinkedList<Long>();

	public Main() {
		init();
		
		long currentTime = System.nanoTime();
		long newTime = System.nanoTime();
		long deltaT;
		long frameLength = 16666667;
		running = true;
		
		while (running) {
			long fullTime = System.nanoTime();
			currentTime = System.nanoTime();
			newTime = currentTime + frameLength;
			update(frameLength / (double) 1E9);
			render();
			currentTime = System.nanoTime();
			while (currentTime < newTime) {
				try {
					Thread.sleep((newTime - currentTime) / 1000000);
					currentTime = System.nanoTime();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			timeDelays.add(System.nanoTime() - fullTime);
			if (timeDelays.size() > 50) {
				timeDelays.remove(0);
			}
			double average = 0;
			for (Long delay : timeDelays) {
				average += delay;
			}
			average /= timeDelays.size();
			double fps = 1000000000 / average;
			panel.setFPS(fps);
		}
		
		window.add(panel);
	}
	
	public void addBoid() {
		Vector position = Vector.random(2, 0, 800);
		Vector velocity = Vector.random(2, 0, 0);
		Boid boid = new Boid(position, velocity);
		panel.addRenderable(boid);
		boids.add(boid);
		listener.addBoid(boid);
		preventer.addBoid(boid);
	}
	
	public void addBoidNextUpdate(int num) {
		numBoidsToCreate += num;
	}
	
	public void init() {
		window = new JFrame();
		window.setSize(1600, 1000);
        double zoom = 0.10;
		AffineTransform transform = new AffineTransform();
		transform.scale(zoom, zoom);
		transform.translate(window.getWidth() * zoom, window.getHeight() * zoom);
		panel = new SteeringPanel(transform);
		
		preventer = new CollisionPreventer();
		List<CircleObstacle> obs = new ArrayList<CircleObstacle>();
		for (int i = 0; i < 20; i++) {
			CircleObstacle ob = new CircleObstacle(Vector.random(2, 0, 800), Math.random() * 30);
			//obs.add(ob);
			//preventer.addObstacle(ob);
			panel.addRenderable(ob);
		}
		
		try {
			listener = new BoidMouseListener(transform.createInverse());
		} catch (Exception e) {
			
		}
		
		boids = new ArrayList<Boid>();
		for (int i = 0; i < 200; i++) {
			addBoid();
		}
		
		panel.addMouseListener(listener);
		panel.addMouseMotionListener(listener);
		panel.setFocusable(true);
		panel.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W) {
					addBoidNextUpdate(1);
				}
				if (e.getKeyCode() == KeyEvent.VK_E) {
					addBoidNextUpdate(10);
				}
				if (e.getKeyCode() == KeyEvent.VK_R) {
					addBoidNextUpdate(100);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		window.add(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	public void update(double deltaT) {
		for (Boid boid : boids) {
			Vector obstacleForce = preventer.handlePotentialCollisions(boid);
			if (obstacleForce == null) {
				boid.applyForce(flock(boid));
				boid.wander();
			} else {
				boid.wander();
				//boid.applyForce(obstacleForce);
			}
		}
		
		for (Boid boid : boids) {
			boid.move(deltaT);
		}
				
		while (numBoidsToCreate > 0) {
			addBoid();
			numBoidsToCreate--;
			System.out.println("Created boid");
		}
	}
	
	public Vector flock(Boid boid) {
		Vector averagePosition = new Vector(0, 0);
		Vector averageForward = new Vector(0, 0);
		
		Vector cohesionForce = new Vector(0, 0);
		Vector separationForce = new Vector(0, 0);
		Vector orientationForce = new Vector(0, 0);
		
		int cohesionNeighbors = 0;
		int orientationNeighbors = 0;
		int separationNeighbors = 0;
		
		double collisionRadius = 30;
		double orientationRadius = 300;
		double cohesionRadius = 300;
		
		for (Boid boid2 : boids) {
			if (boid.equals(boid2)) {
				continue;
			}
			Vector displacement = boid2.getPosition().add(boid.getPosition().scale(-1));
			double angle = Math.acos(boid.getForward().dot(displacement) / (displacement.mag() * boid.getForward().mag()));
			//if (angle > 3 * Math.PI / 4) continue;
			
			if (displacement.mag2() < collisionRadius * collisionRadius) {
				separationForce = separationForce.add(displacement.scale(-1 / displacement.mag2()));
				separationNeighbors++;
			}
			if (displacement.mag2() < cohesionRadius * cohesionRadius) {
				averagePosition = averagePosition.add(boid2.getPosition());
				cohesionNeighbors++;
			}
			if (displacement.mag2() < orientationRadius * orientationRadius) {
				averageForward = averageForward.add(boid2.getForward());
				orientationNeighbors++;
			}

		}
					
		if (cohesionNeighbors > 0) {
			averagePosition = averagePosition.scale(1.0 / cohesionNeighbors);
			cohesionForce = averagePosition.add(boid.getPosition().scale(-1));
			cohesionForce = cohesionForce.normalize();
		}

		if (separationNeighbors > 0) {
			separationForce = separationForce.normalize();
		}

		if (orientationNeighbors > 0) {
			averageForward = averageForward.scale(1.0 / orientationNeighbors);
			orientationForce = averageForward.add(boid.getForward().scale(-1));
			orientationForce = orientationForce.normalize();
		}
		
		//Total force is a linear combination of individual forces	
		double separationConstant = 3;
		double cohesionConstant = 2;
		double orientationConstant = 2;

        separationForce = separationForce.scale(separationConstant);
        cohesionForce = cohesionForce.scale(cohesionConstant);
        orientationForce = orientationForce.scale(orientationConstant);
		
		Vector totalForce = separationForce
                                .add(cohesionForce)
                                .add(orientationForce);
								
		return totalForce;
	}
	
	public void render() {
		panel.repaint();
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}
	
}
