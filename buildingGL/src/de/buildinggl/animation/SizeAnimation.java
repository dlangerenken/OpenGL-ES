package de.buildinggl.animation;

public class SizeAnimation extends ValueAnimation {

	public SizeAnimation(int repeatCount, long timeToFinish, float startSizeX,
			float startSizeY, float startSizeZ, float endSizeX, float endSizeY,
			float endSizeZ) {
		super(repeatCount, timeToFinish, new float[] { startSizeX, startSizeY,
				startSizeZ }, new float[] { endSizeX, endSizeY, endSizeZ });
	}

}
