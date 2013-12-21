package de.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import melb.mSafe.model.Element3D;
import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Polygon3D;
import melb.mSafe.model.Triangle;
import de.buildinggl.utilities.ShaderProgram;

public class Layer3DGL implements IDrawableObject {
	private Layer3D layer;
	private boolean visible = true;
	private float[] floorColor = { 34f / 255f, 47f / 255f, 60f / 255f, 1.0f };
	private float[] wallColor = { 255f / 255f, 255f / 255f,
			255f / 255f, 150f / 255f};
	private List<IDrawableObject> drawableObjects;

	public Layer3DGL(Layer3D layer) {
		this.layer = layer;
		init();
	}

	public void init() {
		drawableObjects = new ArrayList<IDrawableObject>();
		for (Polygon3D polygon : layer.polygons) {
			List<Triangle> outlineTriangles = new ArrayList<Triangle>();
			List<Triangle> inlineTriangles = new ArrayList<Triangle>();
			for (Element3D el : polygon.outlines) {
				outlineTriangles.addAll(el.triangles);
			}
			for (Element3D el : polygon.inlines) {
				inlineTriangles.addAll(el.triangles);
			}
			for (Triangle triangle : outlineTriangles) {
				triangle.color = wallColor;
			}
			for (Triangle triangle : inlineTriangles) {
				triangle.color = floorColor;
			}

			DrawableObject outlines = new DrawableObject(outlineTriangles);
			DrawableObject inlines = new DrawableObject(inlineTriangles);
			drawableObjects.add(outlines); // wall
			drawableObjects.add(inlines); // floor
		}
	}

	@Override
	public void draw(float[] mvpMatrix, ShaderProgram program) {
		if (visible) {
			for (IDrawableObject drawableObject : drawableObjects) {
				drawableObject.draw(mvpMatrix, program);
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


}
