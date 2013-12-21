package de.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Model3D;
import melb.mSafe.model.Node;
import melb.mSafe.model.Vector3D;
import melb.mSafe.model.Way;
import android.content.Context;
import android.opengl.GLES20;
import de.buildinggl.utilities.AttributeColorShaderProgram;

public class Model3DGL {
	private List<Layer3DGL> glLayers;
	private boolean linesVisible = true;
	private float modelRatio = 1.0f;
	private Model3D model;
	private IDrawableObject linesWay;
	private List<WayGL> glWays;
	private Vector3D userPosition;
	private IDrawableObject glUserPosition;
	private AttributeColorShaderProgram colorProgram;

	public Model3DGL(Model3D model) {
		this.model = model;
		getModelRatio();
		initLayers();
		// initWays(); //TODO bugfix needed
		initUserPosition();
	}

	private void initUserPosition() {
		userPosition = new Vector3D(0f, 0f, 0f);
		glUserPosition = new UserPositionGL(true, userPosition);
	}

	private void initWays() {
		glWays = new ArrayList<WayGL>();
		List<Node> nodes = new ArrayList<Node>();
		int counter = 0;
		nodes.add(new Node(0, 0, 0, 0));
		nodes.add(new Node(counter++, model.length, 0, 0));
		nodes.add(new Node(counter++, model.length, model.width, 0));
		nodes.add(new Node(counter++, 0, model.width, 0));
		nodes.add(new Node(counter++, 0, 0, 0));

		Way way = new Way(nodes);
		glWays.add(new WayGL(way, new float[] { 1.0f, 0.0f, 0.0f, 1.0f }, true,
				10000));
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

	public void draw(float[] mvpMatrix) {
		if (glLayers != null) {
			for (Layer3DGL layer : glLayers) {
				layer.draw(mvpMatrix, colorProgram);
			}
		}
		if (linesVisible) {
			if (linesWay != null) {
				linesWay.draw(mvpMatrix, colorProgram);
			}
		}
		if (glWays != null) {
			for (WayGL way : glWays) {
				way.draw(mvpMatrix, colorProgram);
			}
		}
		if (glUserPosition != null) {
			glUserPosition.draw(mvpMatrix, colorProgram);
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

	public void setUserPosition(Vector3D userPosition) {
		this.userPosition.set(userPosition);
	}

	public void initWithGLContext(Context context) {
		this.colorProgram = new AttributeColorShaderProgram(context);
	}

}
