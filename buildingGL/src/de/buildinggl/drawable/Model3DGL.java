package de.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Model3D;
import melb.mSafe.model.Vector3D;
import android.content.Context;
import android.opengl.GLES20;
import de.buildinggl.utilities.AttributeColorShaderProgram;

public class Model3DGL {
	private List<Layer3DGL> glLayers;
	private boolean linesVisible = true;
	private float modelRatio = 1.0f;
	private Model3D model;
	private IDrawableObject linesWay;
	public Vector3D userPosition;
	private IDrawableObject glUserPosition;
	private AttributeColorShaderProgram colorProgram;
	private IDrawableObject way;

	public Model3DGL(Model3D model, Vector3D userPosition) {
		this.model = model;
		this.userPosition = userPosition;
		getModelRatio();
		initLayers();
		initUserPosition();
		initWay();
	}

	private void initWay() {
		List<Vector3D> points = new ArrayList<Vector3D>();
		points.add(new Vector3D(0, 0, 0));
		points.add(new Vector3D(model.length, 0f, 0f));
		points.add(new Vector3D(model.length, model.width, 0f));
		points.add(new Vector3D(0f, model.width, 0f));
		points.add(new Vector3D(0, 0, 0));
		way = new WayGL(points, new float[] { 0.0f, 1.0f, 0.0f, 1.0f }, true,
				30000);
	}

	private void initUserPosition() {
		glUserPosition = new UserPositionGL(true, userPosition);
	}

	private void initLayers() {
		glLayers = new ArrayList<Layer3DGL>();
		for (Layer3D layer : model.layers) {
			glLayers.add(new Layer3DGL(layer));
		}
		linesWay = getLinesOfBuilding();
	}

	private IDrawableObject getLinesOfBuilding() {
		float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };
		float[] linesOfBuilding = { 0f, 0f, 0f, color[0], color[1], color[2],
				color[3], model.length, 0f, 0f, color[0], color[1], color[2],
				color[3], model.length, model.width, 0f, color[0], color[1],
				color[2], color[3], 0f, model.width, 0f, color[0], color[1],
				color[2], color[3], 0f, 0f, 0f, color[0], color[1], color[2],
				color[3] };
		DrawableObject modelLine = new DrawableObject(linesOfBuilding,
				GLES20.GL_LINE_STRIP, 0);
		return modelLine;
	}

	public void draw(float[] modelViewProjectionMatrix) {
		if (glLayers != null) {
			for (Layer3DGL layer : glLayers) {
				layer.draw(modelViewProjectionMatrix, colorProgram);
			}
		}
		if (linesVisible) {
			if (linesWay != null) {
				linesWay.draw(modelViewProjectionMatrix, colorProgram);
			}
		}
		if (glUserPosition != null) {
			glUserPosition.draw(modelViewProjectionMatrix, colorProgram);
		}
		if (way != null) {
			way.draw(modelViewProjectionMatrix, colorProgram);
		}

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
		this.model.height = height;
	}

	public void setLength(float length) {
		this.model.length = length;
	}

	public void setWidth(float width) {
		this.model.width = width;
	}

	public void initWithGLContext(Context context) {
		this.colorProgram = new AttributeColorShaderProgram(context);
	}

	public void setUserPosition(Vector3D userPosition) {
		this.userPosition = userPosition;
		((UserPositionGL) glUserPosition).setUserPosition(userPosition);
	}

}
