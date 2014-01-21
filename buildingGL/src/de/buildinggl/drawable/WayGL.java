package de.buildinggl.drawable;

import java.util.List;

import android.opengl.GLES20;
import melb.mSafe.model.Vector3D;
import de.buildinggl.animation.PathAnimation;
import de.buildinggl.utilities.FloatBufferHelper;
import de.buildinggl.utilities.Helper;
import de.buildinggl.utilities.ShaderProgram;

public class WayGL implements IDrawableObject {
	private boolean shouldAnimate;
	private IDrawableObject glWay;
	private IDrawableObject animatedWayArrow;
	private PathAnimation animation;
	private static final float arrowScale = 0.6f;
	private float[] arrowColor;
	private boolean isVisible = true;

	public WayGL(List<Vector3D> points, float[] color, boolean shouldAnimate,
			long timeToFinish) {
		this.shouldAnimate = shouldAnimate;
		this.arrowColor = color;
		float[] linesOfWay = new float[points.size() * 4 * 3];
		for (int i = 0; i < points.size(); i++) {
			// x,y,z
			linesOfWay[(i * 7)] = points.get(i).getX();
			linesOfWay[(i * 7) + 1] = points.get(i).getY();
			linesOfWay[(i * 7) + 2] = points.get(i).getZ();

			// rgba
			linesOfWay[(i * 7) + 3] = color[0];
			linesOfWay[(i * 7) + 4] = color[1];
			linesOfWay[(i * 7) + 5] = color[2];
			linesOfWay[(i * 7) + 6] = color[3];
		}
		glWay = new DrawableObject(linesOfWay, GLES20.GL_LINE_STRIP, 0);

		if (shouldAnimate) {
			animation = new PathAnimation(timeToFinish, PathAnimation.INFINITY,
					points);
			animation.start();
			animatedWayArrow = new DrawableObject(
					FloatBufferHelper.createArrow(arrowScale, arrowColor));
		}
	}

	@Override
	public void draw(float[] mvpMatrix, ShaderProgram program) {
		if (isVisible) {
			glWay.draw(mvpMatrix, program);
			if (shouldAnimate) {
				float[] animatedMatrix = Helper.animateObject(
						animation.animate(), mvpMatrix);
				animatedWayArrow.draw(animatedMatrix, program);
			}
		}
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

}
