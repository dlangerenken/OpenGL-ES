package com.example.touch;

public class Vector3D {

	private float x;
	private float y;
	private float z;

	public Vector3D() {
	}

	public Vector3D(Vector3D v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;

	}

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float getLength() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3D set(Vector3D other) {
		x = other.getX();
		y = other.getY();
		z = other.getZ();
		return this;
	}

	public Vector3D set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3D add(Vector3D value) {
		this.x += value.getX();
		this.y += value.getY();
		this.z += value.getZ();
		return this;
	}

	public static Vector3D subtract(Vector3D lhs, Vector3D rhs) {
		return new Vector3D(lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z);
	}

	public static float getDistance(Vector3D lhs, Vector3D rhs) {
		Vector3D delta = Vector3D.subtract(lhs, rhs);
		return delta.getLength();
	}

	public static float getSignedAngleBetween(Vector3D a, Vector3D b) {
		Vector3D na = getNormalized(a);
		Vector3D nb = getNormalized(b);

		return (float) (Math.atan2(nb.y, nb.x) - Math.atan2(na.y, na.x));
	}

	public static Vector3D getNormalized(Vector3D v) {
		float l = v.getLength();
		if (l == 0)
			return new Vector3D();
		else
			return new Vector3D(v.x / l, v.y / l, v.x / l);

	}

	@Override
	public String toString() {
		return String.format("(%.4f, %.4f, %.4f)", x, y, z);
	}
}
