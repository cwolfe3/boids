import java.text.DecimalFormat;


public class Vector {

	public double[] elements;
	
	public Vector(int n) {
		elements = new double[n];
	}
	
	public Vector(double... elements) {
		this.elements = elements.clone();
	}
	
	public Vector add(Vector v) {
		double[] newElements = new double[elements.length];
		for (int i = 0; i < elements.length; i++) {
			newElements[i] = elements[i] + v.elements[i];
		}
		return new Vector(newElements);
	}
	
	public double dot(Vector v) {
		double dot = 0;
		for (int i = 0; i < elements.length; i++) {
			dot += elements[i] * v.elements[i];
		}
		return dot;
	}
	
	public Vector scale(double s) {
		double[] newElements = new double[elements.length];
		for (int i = 0; i < elements.length; i++) {
			newElements[i] = elements[i] * s;
		}
		return new Vector(newElements);
	}
	
	public double mag2() {
		double mag2 = 0;
		for (int i = 0; i < elements.length; i++) {
			mag2 += elements[i] * elements[i];
		}
		return mag2;
	}
	
	public double mag() {
		return Math.sqrt(mag2());
	}
	
	@Override
	public String toString() {
		DecimalFormat format = new DecimalFormat("0.000");
		StringBuilder b = new StringBuilder();
		b.append('(');
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				b.append(',');
				b.append(' ');
			}
			b.append(format.format(elements[i]));
		}
		b.append(')');
		return new String(b);
	}
	
	public double getElem(int pos) {
		return elements[pos];
	}
	
	public Vector normalize() {
		double mag = mag();
		if (mag > 0) {
			return scale(1 / mag());
		} else {
			return this;
		}
	}
	
	public static Vector random(int n, double low, double high) {
		double[] newElements = new double[n];
		for (int i = 0; i < n; i++) {
			newElements[i] = Math.random() * (high - low) + low;
		}
		return new Vector(newElements);
	}
	
}
