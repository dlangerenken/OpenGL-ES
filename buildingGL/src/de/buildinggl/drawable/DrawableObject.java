package de.buildinggl.drawable;

import java.util.List;

import melb.mSafe.model.Triangle;
import melb.mSafe.model.Vector3D;
import android.opengl.GLES20;
import de.buildinggl.utilities.PositionShaderProgram;
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
	private ShaderProgram program;
	private int vertexCount;

	public DrawableObject(List<Triangle> triangles) {
		float[] vertices = FloatBufferHelper.createPolygon(triangles);
		vertexArray = new VertexArray(vertices);
		vertexCount = vertices.length / 7;
	}

	public DrawableObject(float[] vertices) {
		vertexArray = new VertexArray(vertices);
		vertexCount = vertices.length / 7;
	}

	public DrawableObject(float[] vertices, Integer glType, int offset) {
		vertexArray = new VertexArray(vertices);
		vertexCount = vertices.length / 7;
		this.offset = offset;
		this.glType = glType;
	}

	public DrawableObject(List<Vector3D> points, float[] defaultColor) {
		// TODO iwas mit defaultcolor machen ... anderes shaderprogram nehmen!!
		// wenn nciht jedes dreieck andere farbe haben soll :O siehe tutorial
		// mit farben
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw(float[] mvpMatrix) {
		program.useProgram();

		if (program instanceof PositionShaderProgram) {
			PositionShaderProgram positionProgram = (PositionShaderProgram) program;
			positionProgram.setUniforms(mvpMatrix);
		}
		bindData();
		if (visible) {
			// Draw the triangle
			if (glType != null) {
				GLES20.glDrawArrays(glType, offset, vertexCount);
			} else {
				GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
			}
		}
	}

	private void bindData() {
		if (program instanceof PositionShaderProgram) {
			PositionShaderProgram positionProgram = (PositionShaderProgram) program;
			vertexArray.setVertexAttribPointer(0,
					positionProgram.getPositionAttributeLocation(),
					Constants.POSITION_COMPONENT_COUNT,
					Constants.POSITION_COLOR_STRIDE);
			vertexArray.setVertexAttribPointer(
					Constants.POSITION_COMPONENT_COUNT,
					positionProgram.getColorAttributeLocation(),
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

	@Override
	public void initWithGLContext(ShaderProgram program) {
		this.program = program;
	}

}
