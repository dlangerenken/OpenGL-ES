package de.buildinggl.drawable;

import java.util.List;

import melb.mSafe.model.Triangle;
import melb.mSafe.model.Vector3D;
import android.opengl.GLES20;
import de.buildinggl.utilities.AttributeColorShaderProgram;
import de.buildinggl.utilities.Constants;
import de.buildinggl.utilities.FloatBufferHelper;
import de.buildinggl.utilities.ShaderProgram;
import de.buildinggl.utilities.TextureShaderProgram;
import de.buildinggl.utilities.VertexArray;

public class DrawableObject implements IDrawableObject {
	private boolean visible = true;

	private VertexArray vertexArray;
	private Integer glType = null;
	private int offset = 0;
	private int vertexCount;

	public DrawableObject(List<Triangle> triangles) {
		float[] vertices = FloatBufferHelper.createPolygon(triangles);
		vertexArray = new VertexArray(vertices);
		vertexCount = vertices.length / 7; // TODO abhängig vom shader machen ->
											// polygonsize!! ob rgba oder nicht
	}

	public DrawableObject(float[] vertices) {
		vertexArray = new VertexArray(vertices);
		vertexCount = vertices.length / 7; // TODO abhängig vom shader machen ->
											// polygonsize!! ob rgba oder nicht
	}

	public DrawableObject(float[] vertices, Integer glType, int offset) {
		vertexArray = new VertexArray(vertices);
		vertexCount = vertices.length / 7; // TODO abhängig vom shader machen ->
											// polygonsize!! ob rgba oder nicht
		this.offset = offset;
		this.glType = glType;
	}

	public DrawableObject(List<Vector3D> points, float[] defaultColor) {
		// TODO Auto-generated constructor stub //TODO for ways
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw(float[] drawMatrix, ShaderProgram program) {
		program.useProgram();

		if (program instanceof AttributeColorShaderProgram) {
			AttributeColorShaderProgram positionProgram = (AttributeColorShaderProgram) program;
			positionProgram.setUniformMatrix(drawMatrix);
		}

		bindData(program);
		if (visible) {
			// Draw the triangle
			if (glType != null) {
				GLES20.glDrawArrays(glType, offset, vertexCount);
			} else {
				GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
			}
		}
	}

	private void bindData(ShaderProgram program) {
		if (program instanceof AttributeColorShaderProgram) {
			AttributeColorShaderProgram colorProgram = (AttributeColorShaderProgram) program;
			vertexArray.setVertexAttribPointer(0,
					colorProgram.getPositionAttributeLocation(),
					Constants.POSITION_COMPONENT_COUNT,
					Constants.POSITION_COLOR_STRIDE);
			vertexArray.setVertexAttribPointer(
					Constants.POSITION_COMPONENT_COUNT,
					colorProgram.getColorAttributeLocation(),
					Constants.COLOR_COMPONENT_COUNT,
					Constants.POSITION_COLOR_STRIDE);
		} else if (program instanceof TextureShaderProgram) {
			TextureShaderProgram textureProgram = (TextureShaderProgram) program;
			vertexArray.setVertexAttribPointer(0,
					textureProgram.getPositionAttributeLocation(),
					Constants.POSITION_COMPONENT_COUNT,
					Constants.POSITION_TEXTURE_STRIDE);
			vertexArray.setVertexAttribPointer(
					Constants.POSITION_COMPONENT_COUNT,
					textureProgram.getTextureCoordinatesAttributeLocation(),
					Constants.TEXTURE_COORDINATES_COMPONENT_COUNT,
					Constants.POSITION_TEXTURE_STRIDE);
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
