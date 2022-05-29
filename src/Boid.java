import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;


public class Boid implements Renderable {
	
	private static double neighborhoodAngle = 3 * Math.PI / 4;
	private static int neighborhoodRadius = 100;
	
	private Vector position;
	private Vector forward;
	private Vector velocity;
	private Vector steeringDirection;
	private Vector oldSteeringDirection = new Vector(0, 0);
	
	private Vector wander;
		
	private double maxForce;
	private double maxSpeed;
	
	public Boid() {
		position = new Vector(0, 0);
		forward = rotate(new Vector(0, 1), Math.random() * 2 * Math.PI);
		velocity = new Vector(0, 0);
		steeringDirection = new Vector(0, 0);
		
		maxForce = 100;
		maxSpeed = 300;
		
		wander = new Vector(1, 0);
	}
	
	public Boid(Vector position) {
		this();
		this.position = position;
	}
	
	public Boid(Vector position, Vector velocity) {
		this(position);
		this.velocity = velocity;
	}
	
	public void move(double deltaT) {
		Vector steeringForce = steeringDirection;
		if (steeringForce.mag2() > maxForce * maxForce) {
			steeringForce = steeringDirection.normalize().scale(maxForce);
		}
		Vector acceleration = steeringForce;
		velocity = velocity.add(acceleration);
		if (velocity.mag2() > maxSpeed * maxSpeed) {
			velocity = velocity.normalize().scale(maxSpeed);
		}
		position = position.add(velocity.scale(deltaT).add(acceleration.scale(deltaT)));
		
		forward = velocity.normalize();
		oldSteeringDirection = steeringDirection;
		steeringDirection = new Vector(0, 0);
	}
	
	public boolean inNeighborhood(Vector v) {
		Vector displacement = v.add(getPosition().scale(-1));
		double angle = Math.acos(getForward().dot(displacement) / (displacement.mag() * getForward().mag()));
		return (angle < neighborhoodAngle && displacement.mag2() < neighborhoodRadius * neighborhoodRadius);
	}
	
	public Vector getForward() {
		return forward;
	}
	
	public void seek(Vector seekTo) {
		Vector desiredVelocity = seekTo.add(position.scale(-1)).normalize().scale(velocity.mag());
		steeringDirection = steeringDirection.add(desiredVelocity.add(velocity.scale(-1)));
	}
	
	public void attract(Vector attractTo, double strength) {
		double displacement2 = attractTo.add(position.scale(-1)).mag2();
		Vector desiredVelocity = attractTo.add(position.scale(-1)).normalize().scale(maxSpeed);
		steeringDirection = steeringDirection.add(desiredVelocity.add(velocity.scale(-1)).scale(strength / displacement2));
	}
	
	public void flee(Vector seekTo) {
		Vector desiredVelocity = seekTo.add(position.scale(-1)).normalize().scale(maxSpeed);
		steeringDirection = steeringDirection.add(desiredVelocity.add(velocity.scale(-1)).scale(-1));
	}
	
	public void arrive(Vector arriveTo) {
		Vector offset = arriveTo.add(position.scale(-1));
		double distance = offset.mag();
		double slowingDistance = 10;
		double rampedSpeed = maxSpeed * (distance / slowingDistance);
		double clampedSpeed = Math.min(rampedSpeed, maxSpeed);
		Vector desiredVelocity = offset.scale(distance / clampedSpeed);
		steeringDirection = steeringDirection.add(desiredVelocity.add(velocity.scale(-1)));
	}

	public void wander() {
		wander = wander.add(Vector.random(2, -0.5, 0.5));
		Vector circleCenter = new Vector(1.414, 0);
		double circleRadius = 1;
		Vector projection = wander.add(circleCenter.scale(-1));
		projection = projection.normalize().scale(circleRadius);
		wander = projection.add(circleCenter);
		applyForce(rotate(wander, Math.atan2(forward.getElem(1), forward.getElem(0))).scale(5));
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.BLUE);
		
		int a = 8;
		int b = 4;
		Vector vectorTip = new Vector(0, a / 2);
		Vector vectorSide1 = new Vector(-b / 2, - a / 2);
		Vector vectorSide2 = new Vector(b / 2, - a / 2);
		
		//cos -sin
		//sin  cos
		double angle = Math.atan2(forward.getElem(1), forward.getElem(0)) - Math.PI / 2;
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
		vectorTip = new Vector(
				vectorTip.getElem(0) * cosAngle - vectorTip.getElem(1) * sinAngle,
				vectorTip.getElem(0) * sinAngle + vectorTip.getElem(1) * cosAngle);
		vectorSide1 = new Vector(
				vectorSide1.getElem(0) * cosAngle - vectorSide1.getElem(1) * sinAngle,
				vectorSide1.getElem(0) * sinAngle + vectorSide1.getElem(1) * cosAngle);
		vectorSide2 = new Vector(
				vectorSide2.getElem(0) * cosAngle - vectorSide2.getElem(1) * sinAngle,
				vectorSide2.getElem(0) * sinAngle + vectorSide2.getElem(1) * cosAngle);
		
		vectorTip = vectorTip.add(position);
		vectorSide1 = vectorSide1.add(position);
		vectorSide2 = vectorSide2.add(position);
		
		
		
		int[] tip = new int[] {(int)(vectorTip.getElem(0) + 0.5), (int)(vectorTip.getElem(1) + 0.5)};
		int[] side1 = new int[] {(int)(vectorSide1.getElem(0) + 0.5), (int)(vectorSide1.getElem(1) + 0.5)};
		int[] side2 = new int[] {(int)(vectorSide2.getElem(0) + 0.5), (int)(vectorSide2.getElem(1) + 0.5)};
		
		//g.drawLine((int)vectorTip.getElem(0), (int)vectorTip.getElem(1), (int)(vectorTip.getElem(0) + forward.getElem(0) * 20), (int)(vectorTip.getElem(1) + forward.getElem(1) * 20));
		
		//g.drawLine(tip[0], tip[1], side1[0], side1[1]);
		//g.drawLine(tip[0], tip[1], side2[0], side2[1]);
		//g.drawLine(side1[0], side1[1], side2[0], side2[1]);
		
		g.drawLine(tip[0], tip[1], side1[0], side1[1]);
		g.drawLine(tip[0], tip[1], side2[0], side2[1]);
		g.drawLine(side1[0], side1[1], side2[0], side2[1]);

		Vector worldVelocity = position.add(velocity);
		worldVelocity = worldVelocity.scale(1);
		//g.drawLine((int)position.getElem(0), (int)position.getElem(1), (int)worldVelocity.getElem(0), (int)worldVelocity.getElem(1));
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public void applyForce(Vector force) {
		steeringDirection = steeringDirection.add(force);
	}
	
	public Vector worldToLocal(Vector world) {
		double angle = Math.atan2(forward.getElem(1), forward.getElem(0));
		Vector local = rotate(world.add(position.scale(-1)), -angle);
		return local;
	}
	
	public Vector localToWorld(Vector local) {
		double angle = Math.atan2(forward.getElem(1), forward.getElem(0));
		Vector world = rotate(local, angle).add(position);
		return world;
	}
	
	public Vector rotate(Vector vec, double angle) {
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
		Vector result = new Vector(
				vec.getElem(0) * cosAngle - vec.getElem(1) * sinAngle,
				vec.getElem(0) * sinAngle + vec.getElem(1) * cosAngle);
		return result;
	}
	
}
