package com.example.buildinggl.drawable;

public interface IDrawableObject { //TODO to class Abstract with visible..
	void draw(float[] mvpMatrix);

	boolean isVisible();

	void setVisible(boolean visible);
	void initWithGLContext();
}
