package de.buildinggl.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Helper {

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

	public static String getStringFromRaw(Context c, int raw)
			throws IOException {
		Resources r = c.getResources();
		InputStream is = r.openRawResource(raw);
		String statesText = convertStreamToString(is);
		is.close();
		return statesText;
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = is.read();
		while (i != -1) {
			baos.write(i);
			i = is.read();
		}
		return baos.toString();
	}

	private static float[] copyMatrix(float[] matrix) {
		float[] copyM = new float[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			copyM[i] = matrix[i];
		}
		return copyM;
	}

	public static float[] animateObject(float[] animateResult, float[] mvpMatrix) {
		float[] newMatrix = copyMatrix(mvpMatrix);
		if (animateResult != null) {
			Matrix.translateM(newMatrix, 0, animateResult[2], animateResult[3],
					animateResult[4]);
			rotateModel(newMatrix, null, null, animateResult[0], true, 20f, // TODO
																			// values
					30f, 0f);
		}
		return newMatrix;
	}

	public static float[] scaleModel(float[] animateResult, float[] mvpMatrix) {
		float[] newMatrix = copyMatrix(mvpMatrix);
		if (animateResult != null) {
			Matrix.scaleM(newMatrix, 0, animateResult[0], animateResult[1],
					animateResult[2]);
		}
		return newMatrix;
	}

	public static float[] translateModel(float[] animateResult,
			float[] mvpMatrix) {
		float[] newMatrix = copyMatrix(mvpMatrix);
		if (animateResult != null) {
			Matrix.translateM(newMatrix, 0, animateResult[0], animateResult[1],
					animateResult[2]);
		}
		return newMatrix;
	}

	public static float[] translateModel(float x, float y, float z,
			float[] matrix) {
		return translateModel(new float[] { x, y, z }, matrix);
	}

	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call
	 * just after making it:
	 * 
	 * <pre>
	 * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
	 * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
	 * </pre>
	 * 
	 * If the operation is not successful, the check throws an error.
	 * 
	 * @param glOperation
	 *            - Name of the OpenGL call to check.
	 */
	public static void checkGlError(String TAG, String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}
}
