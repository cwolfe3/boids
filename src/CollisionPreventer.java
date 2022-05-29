import java.util.ArrayList;
import java.util.List;


public class CollisionPreventer {

	private List<CircleObstacle> obstacles;
	private List<Boid> boids;
	
	public CollisionPreventer() {
		obstacles = new ArrayList<CircleObstacle>();
		boids = new ArrayList<Boid>();
	}
	
	public void addObstacle(CircleObstacle obstacle) {
		obstacles.add(obstacle);
	}
	
	public void addBoid(Boid boid) {
		boids.add(boid);
	}
	
//	public Vector handlePotentialCollisions(Boid boid) {
//		for (CircleObstacle obstacle : obstacles) {
//			Vector relPosition = boid.worldToLocal(obstacle.getPosition());
//			int objectWidth = (int)(obstacle.getRadius() * 2);
//			if (relPosition.getElem(1) - obstacle.getRadius() > objectWidth || relPosition.getElem(1) + obstacle.getRadius() < -objectWidth) {
//				//continue;
//			}
//			if (relPosition.getElem(0) - obstacle.getRadius() > 100 || relPosition.getElem(0) + obstacle.getRadius() < 0) {
//				continue;
//			}
//			Vector force = boid.localToWorld(new Vector(0, -relPosition.getElem(1)));
//			return force;
//		}
//		return null;
//	}
	
	public Vector handlePotentialCollisions(Boid boid) {
		double closestCollision = Double.MAX_VALUE;
		double direction = 0;
		for (CircleObstacle obstacle : obstacles) {
			Vector relPosition = boid.worldToLocal(obstacle.getPosition());
			Vector closestPoint = relPosition.add(new Vector(Math.min(boid.getVelocity().mag() * 1.5, relPosition.getElem(0)), 0).scale(-1));
			double dist2 = closestPoint.mag2();
			if (dist2 < obstacle.getRadius() * obstacle.getRadius() && dist2 < closestCollision) {
				closestCollision = Math.min(boid.getVelocity().mag() * 1.5, relPosition.getElem(0));
				direction = obstacle.getPosition().getElem(1);
			}
			
		}
		if (closestCollision < Double.MAX_VALUE) {
			Vector force = boid.localToWorld(new Vector(0, direction)).add(boid.getPosition().scale(-1)).normalize().scale(1000000);
			//System.out.println(force);
			return force;
		}
		return null;
	}
	
}
