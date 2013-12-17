package com.example.buildinggl.drawable;

public interface IDrawableObject {
	void draw(float[] mvpMatrix);

	boolean isVisible();

	void setVisible(boolean visible);
	void initWithGLContext();
}
