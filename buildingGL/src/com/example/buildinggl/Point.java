package com.example.buildinggl;

public class Point {
	public float x;
	public float y;
	public float z;

	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float[] getXYZ() {
		return new float[] { x, y, z };
	}
}
