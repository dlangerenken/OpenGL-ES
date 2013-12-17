package com.example.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Node;
import melb.mSafe.model.Triangle;
import melb.mSafe.model.Way;

import com.example.buildinggl.GLHelper;
import com.example.buildinggl.PathAnimation;
import com.example.buildinggl.Point;

public class WayGL implements IDrawableObject {

	private Way way;
	private boolean shouldAnimate;
	private IDrawableObject glWay;
	private IDrawableObject animatedWay;
	private PathAnimation animation;
	private long timeToFinish;

	private float[] defaultColor;
	private float[] arrowColor;
	private boolean isVisible = true;

	public WayGL(Way way, float[] color, boolean shouldAnimate,
			long timeToFinish) {
		this.way = way;
		this.shouldAnimate = shouldAnimate;
		this.arrowColor = color;
		this.defaultColor = color;
		this.defaultColor[3] = 0.5f;
		this.timeToFinish = timeToFinish;
		
		List<Point> points = new ArrayList<Point>();
		for (Node node : way.getPoints()) {
			points.add(new Point(node.getX(), node.getY(), node.getZ()));
		}
		glWay = new DrawableLine(points, defaultColor);
		
		if (shouldAnimate) {
			animation = new PathAnimation(points, timeToFinish,
					PathAnimation.INFINITY);
			animation.start();
			List<Triangle> triangles = new ArrayList<Triangle>();
			// first part of arrow
			triangles.add(new Triangle(new float[] { 25f, 15f, 0f, 10f, 15f,
					0f, 0f, 0f, 0f }, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }));
			// second part of arrow
			triangles.add(new Triangle(new float[] { 25f, 15f, 0f, 0f, 30f, 0f,
					10f, 15f, 0f }, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }));
			animatedWay = new DrawableObject(triangles, arrowColor);
		}
	}

	@Override
	public void draw(float[] mvpMatrix) {
		if (isVisible) {
			glWay.draw(mvpMatrix);
			if (shouldAnimate) {
				float[] animatedMatrix = GLHelper.animateObject(
						animation.animate(), mvpMatrix);
				animatedWay.draw(animatedMatrix);
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

	@Override
	public void initWithGLContext() {
		glWay.initWithGLContext();
		if (shouldAnimate) {
			animatedWay.initWithGLContext();
		}
	}

}
