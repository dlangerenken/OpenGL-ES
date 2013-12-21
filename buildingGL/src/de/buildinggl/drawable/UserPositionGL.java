package de.buildinggl.drawable;

import de.buildinggl.animation.SizeAnimation;
import de.buildinggl.utilities.FloatBufferHelper;
import de.buildinggl.utilities.Helper;
import de.buildinggl.utilities.ShaderProgram;
import android.opengl.GLES20;
import melb.mSafe.model.Vector3D;

public class UserPositionGL implements IDrawableObject {

	private static final float arrowScale = 0.6f;
	private static final float circleRadius = 9;
	private boolean visible;
	private IDrawableObject directionArrow;
	private IDrawableObject currentPositionRadius;
	private boolean shouldAnimate;
	private SizeAnimation animation;
	private Vector3D userPosition;
	private float[] arrowColor = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] circleColor = { 0.8f, 0.77f, 0.8f, 1.0f };
	private float postionOffset = 50; // otherwise collision with floors

	public UserPositionGL(boolean shouldAnimate, Vector3D userPosition) {
		this.shouldAnimate = shouldAnimate;
		this.userPosition = userPosition;
		if (shouldAnimate) {
			animation = new SizeAnimation(SizeAnimation.INFINITY, 2000, 1f, 1f,
					1f, 3f, 3f, 3f);
			animation.start();
		}
		directionArrow = new DrawableObject(FloatBufferHelper.createArrow(
				arrowScale, arrowColor));
		currentPositionRadius = new DrawableObject(
				FloatBufferHelper.createCircle(25, circleRadius, 0, 0, 0,
						circleColor), GLES20.GL_TRIANGLE_FAN, 0); // GLES20.GL_LINE_LOOP,
																	// 2
	}

	@Override
	public void draw(float[] mvpMatrix, ShaderProgram program) {
		float[] translateMatrix = Helper.translateModel(new float[] {
				userPosition.getX(), userPosition.getY(),
				userPosition.getZ() + postionOffset }, mvpMatrix);
		if (shouldAnimate) {
			float[] newValues = animation.animate();
			currentPositionRadius.draw(
					Helper.scaleModel(newValues, translateMatrix), program);
		}
		/*
		 * first get center of arrow
		 */
		directionArrow.draw(Helper.translateModel(-10f * arrowScale, -15f
				* arrowScale, 1f, translateMatrix), program);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
