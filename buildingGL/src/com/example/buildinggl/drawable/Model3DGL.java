package com.example.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Model3D;
import melb.mSafe.model.Triangle;
import android.graphics.Color;
import android.opengl.Matrix;
import android.renderscript.Matrix4f;

import com.example.buildinggl.GLHelper;
import com.example.buildinggl.PathAnimation;
import com.example.buildinggl.Point;

public class Model3DGL implements IDrawableObject {
	private List<Layer3DGL> glLayers;
	private boolean visible = true;
	private boolean linesVisible = true;
	private boolean buildingVisible = false;
	private float modelRatio = 1.0f;
	private Model3D model;
	private IDrawableObject linesOfBuilding;
	private List<IDrawableObject> pathObjects;
	private List<Point> points;

	public Model3DGL(Model3D model) {
		this.model = model;
		getModelRatio();
		initLayers();
		initAnimationExample();
	}

	private PathAnimation animation;

	private void initAnimationExample() {
		pathObjects = new ArrayList<IDrawableObject>();
		animation = new PathAnimation(points, 20000, PathAnimation.INFINITY);
		List<Triangle> triangles = new ArrayList<Triangle>();
		// first part of arrow
		triangles.add(new Triangle(new float[] { 25f, 15f, 0f, 10f, 15f, 0f,
				0f, 0f, 0f }, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }));
		// second part of arrow
		triangles.add(new Triangle(new float[] { 25f, 15f, 0f, 0f, 30f, 0f,
				10f, 15f, 0f }, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }));
		IDrawableObject pathObject = new DrawableObject(triangles, Color.BLUE);
		pathObjects.add(pathObject);
	}

	public void initWithGLContext() {
		for (Layer3DGL glLayer : glLayers) {
			glLayer.initWithGLContext();
		}
		linesOfBuilding.initWithGLContext();
		for (IDrawableObject pathObject : pathObjects) {
			pathObject.initWithGLContext();
		}
		animation.start();
	}

	private void initLayers() {
		glLayers = new ArrayList<Layer3DGL>();
		for (Layer3D layer : model.layers) {
			glLayers.add(new Layer3DGL(layer));
		}
		linesOfBuilding = getLinesOfBuilding();
	}

	private IDrawableObject getLinesOfBuilding() {
		points = new ArrayList<Point>();
		points.add(new Point(0, 0, 0));
		points.add(new Point(model.length, 0, 0));
		points.add(new Point(model.length, model.width, 0));
		points.add(new Point(0, model.width, 0));
		points.add(new Point(0, 0, 0));
		DrawableLine modelLine = new DrawableLine(points, Color.RED);
		return modelLine;
	}

	@Override
	public void draw(float[] mvpMatrix) {
		if (visible) {
			if (buildingVisible) {
				for (Layer3DGL layer : glLayers) {
					layer.draw(mvpMatrix);
				}
			}
			if (linesVisible) {
				linesOfBuilding.draw(mvpMatrix);
			}
		}

		float[] animateResult = animation.animate();
		for (IDrawableObject pathObject : pathObjects) {
			float[] mvpCopy = new float[16];
			for (int i = 0; i < mvpMatrix.length; i++) {
				mvpCopy[i] = mvpMatrix[i];
			}
			if (animateResult != null) {
				Matrix.translateM(mvpCopy, 0, animateResult[2],
						animateResult[3], animateResult[4]);
				GLHelper.rotateModel(mvpCopy, null, null, animateResult[0],
						true, 20f, 30f, 0f);
				pathObject.draw(mvpCopy);
			}
		}

	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public float getRatio() {
		return modelRatio;
	}

	public float getWidth() {
		return model.width;
	}

	public float getHeight() {
		return model.height;
	}

	public float getLength() {
		return model.length;
	}

	private void getModelRatio() {
		float highestValue = (model.width > model.length) ? model.width
				: model.length;
		modelRatio = 2f / highestValue;
	}

	public List<Layer3DGL> getLayers() {
		return glLayers;
	}

	public void setHeight(float height) {
		model.height = height;
	}

	public void setLength(float length) {
		model.length = length;
	}

	public void setWidth(float width) {
		model.width = width;
	}

}
