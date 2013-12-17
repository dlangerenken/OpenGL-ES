package com.example.buildinggl;

import android.opengl.Matrix;

public class GLHelper {

	public static void rotateModel(float[] mModelMatrix, Float x, Float y, Float z,
			boolean rotateAroundCenter, Float width, Float length, Float height) {
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
}
