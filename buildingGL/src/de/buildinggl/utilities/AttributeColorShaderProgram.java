/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package de.buildinggl.utilities;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import android.content.Context;
import de.buildinggl.R;

public class AttributeColorShaderProgram extends ShaderProgram {
	// Uniform locations
	private final int uMatrixLocation;

	// Attribute locations
	private final int aPositionLocation;
	private final int aColorLocation;

	public AttributeColorShaderProgram(Context context) {
		super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

		// Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

		// Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aColorLocation = glGetAttribLocation(program, A_COLOR);
	}

	public void setUniformMatrix(float[] matrix) {
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
	}

	public void setUniformMatrix(FloatBuffer floatBuffer) {
		glUniformMatrix4fv(uMatrixLocation, 1, false, floatBuffer);
	}

	public int getPositionAttributeLocation() {
		return aPositionLocation;
	}

	public int getColorAttributeLocation() {
		return aColorLocation;
	}
}
