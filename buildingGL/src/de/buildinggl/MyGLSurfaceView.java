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
package de.buildinggl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import de.buildinggl.drawable.Model3DGL;
import de.buildinggl.utilities.TouchManager;
import de.buildinggl.utilities.Vector2D;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {
	private Vector2D position = new Vector2D();
	public float scale = 1;
	public float angle = 0;

	private TouchManager touchManager = new TouchManager(2);
	private boolean isInitialized = false;

	private final MyGLRenderer mRenderer;
	private Model3DGL model3dGl;
	private OpenGLViewFragment fragment;

	public MyGLSurfaceView(Context context, Model3DGL model3dGl,
			OpenGLViewFragment openGLViewFragment) {
		super(context);
		this.model3dGl = model3dGl;

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);
		// setPreserveEGLContextOnPause(true);
		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new MyGLRenderer(model3dGl, context);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			touchManager.update(event);

			if (touchManager.getPressCount() == 1) {
				position.add(touchManager.moveDelta(0).rotate(-angle));
			} else if (touchManager.getPressCount() == 2) {
				Vector2D current = touchManager.getVector(0, 1);
				Vector2D previous = touchManager.getPreviousVector(0, 1);
				float currentDistance = current.getLength();
				float previousDistance = previous.getLength();

				if (previousDistance != 0) {
					scale *= currentDistance / previousDistance;
				}
				angle -= Vector2D.getSignedAngleBetween(current, previous);
			}

			invalidate();
		} catch (Throwable t) {
			// throw new RuntimeException(t);
		}

		if (!isInitialized) {
			float w = mRenderer.getWidth();
			float h = mRenderer.getHeight();
			position.set(w / 2, h / 2);
			isInitialized = true;
		}

		mRenderer.setRotation(null, null, (float) Math.toDegrees(angle));
		mRenderer.setScale(scale);
		mRenderer.setTranslation(position.getX(), position.getY(), null);
		return true;
	}

	public MyGLRenderer getRenderer() {
		return mRenderer;
	}

}
