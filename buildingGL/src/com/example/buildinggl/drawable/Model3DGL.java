package com.example.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Model3D;
import melb.mSafe.model.Node;
import melb.mSafe.model.Way;
import android.graphics.Color;

import com.example.buildinggl.Point;

public class Model3DGL implements IDrawableObject {
	private List<Layer3DGL> glLayers;
	private boolean visible = true;
	private boolean linesVisible = true;
	private boolean buildingVisible = true;
	private float modelRatio = 1.0f;
	private Model3D model;
	private List<Point> points;
	private IDrawableObject linesOfBuilding;
	private List<WayGL> glWays;

	public Model3DGL(Model3D model) {
		this.model = model;
		getModelRatio();
		initLayers();
		initWays();
	}

	private void initWays() {
		glWays = new ArrayList<WayGL>();
		List<Node> nodes = new ArrayList<Node>();
		int counter = 0;
		for (Point point : points) {
			nodes.add(new Node(counter++, point.x, point.y, point.z));
		}
		Way way = new Way(nodes);

		glWays.add(new WayGL(way, new float[] { 1.0f, 0.0f, 0.0f, 1.0f }, true,
				10000));
	}

	public void initWithGLContext() {
		for (Layer3DGL glLayer : glLayers) {
			glLayer.initWithGLContext();
		}
		linesOfBuilding.initWithGLContext();

		for (WayGL way : glWays) {
			way.initWithGLContext();
		}
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
			for (WayGL way : glWays) { //TODO maybe only layer
				way.draw(mvpMatrix);
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
