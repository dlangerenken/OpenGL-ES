package com.example.buildinggl.drawable;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import melb.mSafe.model.Element3D;
import melb.mSafe.model.Layer3D;
import melb.mSafe.model.Polygon3D;
import melb.mSafe.model.Triangle;

public class Layer3DGL extends Layer3D implements IDrawableObject {
	private Layer3D layer;
	private boolean visible = true;
	private int floorColor = Color.rgb(34, 47, 60);
	private int wallColor = Color.argb(150, 255, 255, 255);
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
			DrawableObject outlines = new DrawableObject(outlineTriangles,
					wallColor);
			DrawableObject inlines = new DrawableObject(inlineTriangles,
					floorColor);
			drawableObjects.add(outlines); // wall
			drawableObjects.add(inlines); // floor
		}
	}

	@Override
	public void draw(float[] mvpMatrix) {
		if (visible) {
			for (IDrawableObject drawableObject : drawableObjects) {
				drawableObject.draw(mvpMatrix);
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

	@Override
	public void initWithGLContext() {
		for (IDrawableObject drawableObject : drawableObjects) {
			drawableObject.initWithGLContext();
		}
	}

}
