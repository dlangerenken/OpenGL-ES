package de.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Model3D;
import melb.mSafe.model.Node;
import melb.mSafe.model.Vector3D;
import melb.mSafe.model.Way;
import android.opengl.GLES20;
import de.buildinggl.utilities.ShaderProgram;

public class Model3DGL implements IDrawableObject {
	private List<Layer3DGL> glLayers;
	private boolean visible = true;
	private boolean linesVisible = true;
	private boolean buildingVisible = true;
	private float modelRatio = 1.0f;
	private Model3D model;
	private IDrawableObject linesWay;
//	private List<WayGL> glWays;
	private Vector3D userPosition;
	private IDrawableObject glUserPosition;

	public Model3DGL(Model3D model) {
		this.model = model;
		getModelRatio();
		initLayers();
//		initWays();
		initUserPosition();
	}

	private void initUserPosition() {
		userPosition = new Vector3D(0f, 0f, 0f);
		glUserPosition = new UserPositionGL(true, userPosition);
	}

//	private void initWays() {
//		glWays = new ArrayList<WayGL>();
//		List<Node> nodes = new ArrayList<Node>();
//		int counter = 0;
//		for (Vector3D point : points) {
//			nodes.add(new Node(counter++, point.getX(), point.getY(), point
//					.getZ()));
//		}
//		Way way = new Way(nodes);
//
//		glWays.add(new WayGL(way, new float[] { 1.0f, 0.0f, 0.0f, 1.0f }, true,
//				10000));
//	}

	@Override
	public void initWithGLContext(ShaderProgram colorProgram) {
		for (Layer3DGL glLayer : glLayers) {
			glLayer.initWithGLContext(colorProgram);
		}
		linesWay.initWithGLContext(colorProgram);

//		for (WayGL way : glWays) {
//			way.initWithGLContext(colorProgram);
//		}
		glUserPosition.initWithGLContext(colorProgram);
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

	@Override
	public void draw(float[] mvpMatrix) {
		if (visible) {
			if (buildingVisible) {
				for (Layer3DGL layer : glLayers) {
					layer.draw(mvpMatrix);
				}
			}
			if (linesVisible) {
				linesWay.draw(mvpMatrix);
			}
//			for (WayGL way : glWays) { // TODO maybe only layer
//				way.draw(mvpMatrix);
//			}
			glUserPosition.draw(mvpMatrix);
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

	public void setUserPosition(Vector3D userPosition) {
		this.userPosition.set(userPosition);
	}

}
