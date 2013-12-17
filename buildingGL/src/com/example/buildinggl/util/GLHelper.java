package com.example.buildinggl.util;

import android.opengl.Matrix;

public class GLHelper {

	public static void rotateModel(float[] mModelMatrix, Float x, Float y,
			Float z, boolean rotateAroundCenter, Float width, Float length,
			Float height) {
		// translation for rotating the model around its center
		if (rotateAroundCenter) {
			Matrix.translateM(mModelMatrix, 0, length / 2f, width / 2f,
					height / 2f);
		}
		if (x != null) {
			Matrix.rotateM(mModelMatrix, 0, x, 1.0f, 0.0f, 0.0f);
		}
		if (y != null) {
			Matrix.rotateM(mModelMatrix, 0, y, 0.0f, 1.0f, 0.0f);
		}
		if (z != null) {
			Matrix.rotateM(mModelMatrix, 0, z, 0.0f, 0.0f, 1.0f);
		}

		// translation back to the origin
		if (rotateAroundCenter) {
			Matrix.translateM(mModelMatrix, 0, -length / 2, -width / 2f,
					-height / 2f);
		}
	}

	public static float[] animateObject(float[] animateResult, float[] mvpMatrix) {
		float[] copyM = new float[mvpMatrix.length];
		for (int i = 0; i < mvpMatrix.length; i++) {
			copyM[i] = mvpMatrix[i];
		}
		if (animateResult != null) {
			Matrix.translateM(copyM, 0, animateResult[2], animateResult[3],
					animateResult[4]);
			rotateModel(copyM, null, null, animateResult[0], true, 20f, 30f, 0f);
		}
		return copyM;
	}
}
