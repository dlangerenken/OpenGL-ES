package com.example.buildinggl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.touch.Vector3D;

public class PathAnimation2 {
	public static final int INFINITY = -1;
	private static final float epsilon = 0.1f;

	private Vector3D[] currentPosition;
	private int[] nextVector;
	private Vector3D[] nextPathElement;
	private List<Vector3D> pointsOnPath;
	private float[][] lastValues;

	private long[] currentTime;
	private float timeToFinish;
	private float pathLength;

	private boolean isFinished = false;
	private boolean isRunnning = false;

	private int repeatCount = 1;
	private int currentRepeatCount[];
	private int size;

	public PathAnimation2(List<Vector3D> pointsOnPath, float timeToFinish,
			int repeatCount, int size) {
		this.size = size;
		this.pointsOnPath = pointsOnPath;
		this.timeToFinish = timeToFinish;
		this.repeatCount = repeatCount;

		this.nextVector = new int[size];
		this.currentPosition = new Vector3D[size];
		this.nextPathElement = new Vector3D[size];
		this.currentTime = new long[size];

		for (int i = 0; i < size; i++) {
			nextVector[i] = 0;
			currentPosition[i] = pointsOnPath.get(nextVector[i]++);
			currentTime[i] = new Date().getTime()
					+ ((int) timeToFinish / size * i);
		}
		calculatePathLength();
	}

	public PathAnimation2(List<Point> points, int timeToFinish,
			int repeatCount, int size) {
		List<Vector3D> vectors = new ArrayList<Vector3D>();
		for (Point point : points) {
			vectors.add(new Vector3D(point.x, point.y, point.z));
		}
		this.size = size;
		this.lastValues = new float[size][5];
		this.pointsOnPath = vectors;
		this.timeToFinish = timeToFinish;
		this.repeatCount = repeatCount;

		this.nextVector = new int[size];
		this.currentPosition = new Vector3D[size];
		this.nextPathElement = new Vector3D[size];
		this.currentTime = new long[size];

		for (int i = 0; i < size; i++) {
			nextVector[i] = 0;
			currentPosition[i] = pointsOnPath.get(nextVector[i]++);
			nextPathElement[i] = pointsOnPath.get(nextVector[i]);
			currentTime[i] = new Date().getTime()
					+ ((int) timeToFinish / size * i);
		}
		calculatePathLength();
	}

	public void start() {
		for (int i = 0; i < size; i++) {
			this.currentTime[i] = new Date().getTime()
					+ ((int) timeToFinish / size * i);
			isRunnning = true;
		}

	}

	public void pause() {
		isRunnning = false;
	}

	public void reset(int i) {
		this.nextVector[i] = 0;
		nextVector[i] = 0;
		currentPosition[i] = pointsOnPath.get(nextVector[i]++);
		nextPathElement[i] = pointsOnPath.get(nextVector[i]);
		currentTime[i] = new Date().getTime() + ((int) timeToFinish / size * i);
	}

	public void reset() {
		for (int i = 0; i < size; i++) {
			this.nextVector[i] = 0;
			nextVector[i] = 0;
			currentPosition[i] = pointsOnPath.get(nextVector[i]++);
			nextPathElement[i] = pointsOnPath.get(nextVector[i]);
			currentTime[i] = new Date().getTime()
					+ ((int) timeToFinish / size * i);
		}
		this.isFinished = false;
	}

	private void calculatePathLength() {
		float distance = 0.0f;
		for (int i = 0; i < pointsOnPath.size() - 1; i++) {
			distance += Vector3D.getDistance(pointsOnPath.get(i),
					pointsOnPath.get(i + 1));
		}
		this.pathLength = distance;
	}

	/**
	 * 
	 * @return float[](yaw,pitch,x,y,z)
	 */
	public float[][] animate() {
		if (isRunnning && !isFinished) {
			for (int i = 0; i < size; i++) {
				long timeNow = new Date().getTime();
				long delta = timeNow - currentTime[i];
				currentTime[i] = timeNow;

				double dX = nextPathElement[i].getX()
						- currentPosition[i].getX();
				double dY = nextPathElement[i].getY()
						- currentPosition[i].getY();
				double dZ = nextPathElement[i].getZ()
						- currentPosition[i].getZ();

				float yaw = (float) Math.atan2(dY, dX);
				float pitch = (float) (Math.atan2(Math.sqrt(dX * dX + dY * dY),
						dZ) + Math.PI);

				float pathToWalk = pathLength / timeToFinish * delta;
				float distanceToNextPathElement = Vector3D.getDistance(
						currentPosition[i], nextPathElement[i]);
				while (pathToWalk > distanceToNextPathElement - epsilon) {
					currentPosition[i] = nextPathElement[i];
					pathToWalk -= distanceToNextPathElement;
					nextVector[i]++;
					if (nextVector[i] >= pointsOnPath.size()) {
						isFinished(i);
						return null;
					}
					nextPathElement[i] = pointsOnPath.get(nextVector[i]);
					distanceToNextPathElement = Vector3D.getDistance(
							currentPosition[i], nextPathElement[i]);
				}

				float percentageToWalk = pathToWalk / distanceToNextPathElement;
				float newX = (nextPathElement[i].getX() - currentPosition[i]
						.getX()) * percentageToWalk;
				float newY = (nextPathElement[i].getY() - currentPosition[i]
						.getY()) * percentageToWalk;
				float newZ = (nextPathElement[i].getZ() - currentPosition[i]
						.getZ()) * percentageToWalk;
				Vector3D difference = new Vector3D(newX, newY, newZ);
				currentPosition[i] = new Vector3D(currentPosition[i]);
				currentPosition[i].add(difference);
				pathToWalk = 0;

				lastValues[i] = new float[] { (float) Math.toDegrees(yaw),
						(float) Math.toDegrees(pitch),
						currentPosition[i].getX(), currentPosition[i].getY(),
						currentPosition[i].getZ() };
				return lastValues;
			}
		}
		return null;
	}

	private void isFinished(int i) {
		currentRepeatCount[i]++;
		if (repeatCount == INFINITY || currentRepeatCount[i] < repeatCount) {
			reset(i);
		} else {
			isFinished = true;
		}
	}
}
