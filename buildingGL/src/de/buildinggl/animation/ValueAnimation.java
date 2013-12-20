package de.buildinggl.animation;

import java.util.Date;

public class ValueAnimation {
	public static final int INFINITY = -1;
	private static final float epsilon = 0.005f;

	private float[] endValues;
	private float[] startValues;
	private float[] currentValues;

	private long currentTime;
	private long timeToFinish;

	private boolean isFinished = false;
	private boolean isRunnning = false;

	private int repeatCount = 1;
	private int currentRepeatCount = 0;

	public ValueAnimation(int repeatCount, long timeToFinish,
			float[] startValues, float[] endValues) {
		this.repeatCount = repeatCount;
		this.currentValues = new float[startValues.length];
		this.currentRepeatCount = 0;
		this.startValues = startValues;
		this.endValues = endValues;
		this.timeToFinish = timeToFinish;
		this.currentTime = new Date().getTime();
		copyStartToCurrent();
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
		this.currentRepeatCount = 0;
		this.currentTime = new Date().getTime();
		copyStartToCurrent();
	}

	private void copyStartToCurrent() {
		for (int i = 0; i < currentValues.length; i++) {
			currentValues[i] = startValues[i];
		}
	}

	/**
	 * 
	 * @return float[](currentValues)
	 */
	public float[] animate() {
		if (isRunnning) {
			long timeNow = new Date().getTime();
			long delta = timeNow - currentTime;
			currentTime = timeNow;

			if (!isFinished) {
				for (int i = 0; i < currentValues.length; i++) {
					currentValues[i] = ((endValues[i] - startValues[i])
							/ timeToFinish * delta)
							+ currentValues[i];
				}
			}
			isFinished();
			return currentValues;
		}
		return null;
	}

	private void isFinished() {
		boolean finished = true;
		for (int i = 0; i < currentValues.length; i++) {
			if (currentValues[i] + epsilon < endValues[i]) {
				finished = false;
			}
		}
		if (finished) {
			currentRepeatCount++;
			if (repeatCount == INFINITY || currentRepeatCount < repeatCount) {
				reset();
			} else {
				isFinished = true;
			}
		}
	}
}
