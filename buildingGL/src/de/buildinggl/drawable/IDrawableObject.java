package de.buildinggl.drawable;

import de.buildinggl.utilities.ShaderProgram;

public interface IDrawableObject { //TODO to class Abstract with visible..
	void draw(float[] modelViewProjectionMatrix, ShaderProgram program);

	boolean isVisible();

	void setVisible(boolean visible);
}
