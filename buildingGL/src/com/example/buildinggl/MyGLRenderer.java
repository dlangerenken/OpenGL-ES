/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.buildinggl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.buildinggl.drawable.Model3DGL;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class must
 * override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = "MyGLRenderer";

	private int mFPS;
	private int lastMFPS;
	private long mLastTime;

	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program. mMVPMatrix is an abbreviation for
	 * "Model View Projection Matrix"
	 */
	private final float[] mMVMatrix = new float[16];

	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	private final float[] mProjectionMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private final float[] mViewMatrix = new float[16];

	private float nearPlaneDistance = 1f;
	private float farPlaneDistance = 200f;

	private int offset = 0;
	private float eyeX = 0;
	private float eyeY = 0;
	private float eyeZ = -1;
	private float centerX = 0f;
	private float centerY = 0f;
	private float centerZ = 0f;
	private float upX = 0f;
	private float upY = 1.0f;
	private float upZ = 0.0f;

	private float mZoomLevel = 1f;

	private float defaultRotationX = 100.0f; // building otherwise on the wrong
												// side
	private float defaultRotationZ = 180.0f; // building otherwise on the wrong
												// side
	private float rotationX = defaultRotationX;
	private float rotationY = 0.0f;
	private float rotationZ = defaultRotationZ;

	private float translateX = 0.0f;
	private float translateY = 0.0f;
	private float translateZ = 0.0f;

	private float scaleFactor = 20.0f; // no matter what scale factor -> it's
										// not possible to zoom into the
										// building...
	private float ratio;
	private float width;
	private float height;

	public Model3DGL model3d;
	private float[] backgroundColor = { 210f / 255f, 228f / 255f, 255f / 255f,
			1.0f };

	public MyGLRenderer(Model3DGL model3d) {
		this.model3d = model3d;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(backgroundColor[0], backgroundColor[1],
				backgroundColor[2], backgroundColor[3]);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		model3d.initWithGLContext();
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		// System.out.println("FPS: " + getFPS());
		long currentTime = System.currentTimeMillis();
		mFPS++;
		if (currentTime - mLastTime >= 1000) {
			lastMFPS = mFPS;
			mFPS = 0;
			mLastTime = currentTime;
		}

		float[] mMVPMatrix = new float[16];

		// Draw background color
		Matrix.setIdentityM(mModelMatrix, 0);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// model is in origin-solution too big
		Matrix.scaleM(mModelMatrix, 0, model3d.getRatio() * scaleFactor,
				model3d.getRatio() * scaleFactor, model3d.getRatio()
						* scaleFactor);
		// Matrix.translateM(mModelMatrix, 0, -1f, -1f, -1f);

		Matrix.translateM(mModelMatrix, 0, translateX, translateY, translateZ);

		GLHelper.rotateModel(mModelMatrix, rotationX, rotationY, rotationZ, true,
				model3d.getWidth(), model3d.getLength(), model3d.getHeight());

		// Set the camera position (View matrix)
		Matrix.setLookAtM(mViewMatrix, offset, eyeX, eyeY, eyeZ / mZoomLevel,
				centerX, centerY, centerZ, upX, upY, upZ);

		// combine the model with the view matrix
		Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, 1, -1,
				nearPlaneDistance, farPlaneDistance);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

		model3d.draw(mMVPMatrix);

	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		// Adjust the viewport based on geometry changes,
		// such as screen rotation
		GLES20.glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;
		ratio = (float) width / height;
	}

	/**
	 * Utility method for compiling a OpenGL shader.
	 * 
	 * <p>
	 * <strong>Note:</strong> When developing shaders, use the checkGlError()
	 * method to debug shader coding errors.
	 * </p>
	 * 
	 * @param type
	 *            - Vertex or fragment shader type.
	 * @param shaderCode
	 *            - String containing the shader code.
	 * @return - Returns an id for the shader.
	 */
	public static int loadShader(int type, String shaderCode) {
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	

	public int getFPS() {
		return lastMFPS;
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
	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	public void setZoom(float zoom) {
		this.mZoomLevel = zoom;
	}

	public void setDistance(float distance) {
		eyeZ = distance;
	}

	public float getDistance() {
		return eyeZ;
	}

	public float getRotationX() {
		return rotationX;
	}

	public void setRotationX(float rotationX) {
		this.rotationX = defaultRotationX + rotationX;
	}

	public float getRotationY() {
		return rotationY;
	}

	public void setRotationY(float rotationY) {
		this.rotationY = rotationY;
	}

	public float getRotationZ() {
		return rotationZ;
	}

	public void setRotationZ(float rotationZ) {
		this.rotationZ = defaultRotationZ + rotationZ;
	}

	public float getFarPlane() {
		return farPlaneDistance;
	}

	public float getNearPlane() {
		return nearPlaneDistance;
	}

	public void addTranslation(float mPosX, float mPosY) {
		this.translateX = mPosX;
		this.translateY = mPosY;
	}

	public void downPressed() {
		translateX -= 10;
	}

	public void upPressed() {
		translateX += 10;
	}

	public void actionMoved(float mPosX, float mPosY) {
		float translationX = (mPosX / width);
		float translationY = -(mPosY / height);
		addTranslation(translationX, translationY);
	}

	public float getmZoomLevel() {
		return mZoomLevel;
	}

	public void setmZoomLevel(float mZoomLevel) {
		this.mZoomLevel = mZoomLevel;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setTranslation(Float x, Float y, Float z) {
		if (x != null) {
			this.translateX = -x;
		}
		if (y != null) {
			this.translateY = y;
		}
		if (z != null) {
			this.translateZ = -z;
		}
	}

	public void setRotation(Float x, Float y, Float z) {
		if (x != null) {
			this.rotationX = defaultRotationX + x;
		}
		if (y != null) {
			this.rotationY = y;
		}
		if (z != null) {
			this.rotationZ = defaultRotationZ + z;
		}
	}

	public void setScale(float scale) {
		this.mZoomLevel = scale;
	}

	public float getDefaultRotationX() {
		return defaultRotationX;
	}
}