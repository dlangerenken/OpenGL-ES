package com.example.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.opengl.Matrix;

import com.example.buildinggl.Point;

import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Model3D;

public class Model3DGL implements IDrawableObject {
	private List<Layer3DGL> glLayers;
	private boolean visible = true;
	private float modelRatio = 1.0f;
	private Model3D model;
	private IDrawableObject linesOfBuilding;

	public Model3DGL(Model3D model) {
		this.model = model;
		getModelRatio();
		initLayers();
	}

	public void initWithGLContext() {
		for (Layer3DGL glLayer : glLayers) {
			glLayer.initWithGLContext();
		}
		linesOfBuilding.initWithGLContext();
	}

	private void initLayers() {
		glLayers = new ArrayList<Layer3DGL>();
		for (Layer3D layer : model.layers) {
			glLayers.add(new Layer3DGL(layer));
		}
		linesOfBuilding = getLinesOfBuilding();
	}

	private IDrawableObject getLinesOfBuilding() {
		List<Point> points = new ArrayList<Point>();
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
		for (Layer3DGL layer : glLayers) {
			layer.draw(mvpMatrix);
		}
		linesOfBuilding.draw(mvpMatrix);
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
