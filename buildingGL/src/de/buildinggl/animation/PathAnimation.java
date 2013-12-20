package de.buildinggl.animation;

import java.util.Date;
import java.util.List;

import melb.mSafe.model.Vector3D;

public class PathAnimation {
	public static final int INFINITY = -1;
	private static final float epsilon = 0.1f;

	private Vector3D currentPosition;
	private int nextVector = 1;
	private Vector3D nextPathElement;
	private List<Vector3D> pointsOnPath;

	private long currentTime;
	private float timeToFinish;
	private float pathLength;

	private boolean isFinished = false;
	private boolean isRunnning = false;

	private int repeatCount = 1;
	private int currentRepeatCount = 0;

	public PathAnimation(long timeToFinish, int repeatCount,
			List<Vector3D> pointsOnPath) {
		this.pointsOnPath = pointsOnPath;
		this.timeToFinish = timeToFinish;
		this.repeatCount = repeatCount;
		this.nextVector = 0;
		this.currentPosition = pointsOnPath.get(nextVector++);
		this.nextPathElement = pointsOnPath.get(nextVector);
		this.currentTime = new Date().getTime();
		calculatePathLength();
	}

	public void start() {
		this.currentTime = new Date().getTime();
		isRunnning = true;
	}

	public void pause() {
		isRunnning = false;
	}

	public void reset() {
		this.isFinished = false;
		this.nextVector = 0;
		this.currentPosition = pointsOnPath.get(nextVector++);
		this.nextPathElement = pointsOnPath.get(nextVector);
		this.currentTime = new Date().getTime();
	}

	private void calculatePathLength() {
		float distance = 0.0f;
		for (int i = 0; i < pointsOnPath.size() - 1; i++) {
			distance += Vector3D.getDistance(pointsOnPath.get(i),
					pointsOnPath.get(i + 1));
		}
		this.pathLength = distance;
	}

	private float[] lastValues = new float[1];

	/**
	 * 
	 * @return float[](yaw,pitch,x,y,z)
	 */
	public float[] animate() {
		if (isRunnning) {
			long timeNow = new Date().getTime();
			long delta = timeNow - currentTime;
			currentTime = timeNow;

			if (!isFinished) {
				double dX = nextPathElement.getX() - currentPosition.getX();
				double dY = nextPathElement.getY() - currentPosition.getY();
				double dZ = nextPathElement.getZ() - currentPosition.getZ();

				float yaw = (float) Math.atan2(dY, dX);
				float pitch = (float) (Math.atan2(Math.sqrt(dX * dX + dY * dY),
						dZ) + Math.PI);

				float pathToWalk = pathLength / timeToFinish * delta;
				float distanceToNextPathElement = Vector3D.getDistance(
						currentPosition, nextPathElement);
				while (pathToWalk > distanceToNextPathElement - epsilon) {
					currentPosition = nextPathElement;
					pathToWalk -= distanceToNextPathElement;
					nextVector++;
					if (nextVector >= pointsOnPath.size()) {
						isFinished();
						return null;
					}
					nextPathElement = pointsOnPath.get(nextVector);
					distanceToNextPathElement = Vector3D.getDistance(
							currentPosition, nextPathElement);
				}

				float percentageToWalk = pathToWalk / distanceToNextPathElement;
				float newX = (nextPathElement.getX() - currentPosition.getX())
						* percentageToWalk;
				float newY = (nextPathElement.getY() - currentPosition.getY())
						* percentageToWalk;
				float newZ = (nextPathElement.getZ() - currentPosition.getZ())
						* percentageToWalk;
				Vector3D difference = new Vector3D(newX, newY, newZ);
				currentPosition = new Vector3D(currentPosition);
				currentPosition.add(difference);
				pathToWalk = 0;

				lastValues = new float[] { (float) Math.toDegrees(yaw),
						(float) Math.toDegrees(pitch), currentPosition.getX(),
						currentPosition.getY(), currentPosition.getZ() };
				return lastValues;
			}
		}
		return null;
	}

	// public float animateOffset(int current){
	// int id = current % numberOfElements;
	// float timeSpawn = timeToFinish / numberOfElements;
	// float delayForId = id*timeSpawn;
	// }

	private void isFinished() {
		currentRepeatCount++;
		if (repeatCount == INFINITY || currentRepeatCount < repeatCount) {
			reset();
		} else {
			isFinished = true;
		}
	}
}
