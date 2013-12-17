package com.example.buildinggl.drawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import com.example.buildinggl.MyGLRenderer;

import melb.mSafe.model.Triangle;
import android.graphics.Color;
import android.opengl.GLES20;

public class DrawableObject implements IDrawableObject {

	private boolean visible = true;
	private final String vertexShaderCode =
	// This matrix member variable provides a hook to manipulate
	// the coordinates of the objects that use this vertex shader
	"uniform mat4 uMVPMatrix;" + "attribute vec4 vPosition;" + "void main() {" +
	// the matrix must be included as a modifier of gl_Position
	// Note that the uMVPMatrix factor *must be first* in order
	// for the matrix multiplication product to be correct.
			"  gl_Position = uMVPMatrix * vPosition;" + "}";

	private final String fragmentShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	private FloatBuffer vertexBuffer;
	private int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	private float[] triangleCoords;
	private int vertexCount;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
															// coordinate

	private float[] color;

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param color
	 */
	private List<Triangle> triangles;

	public DrawableObject(List<Triangle> triangles, float[] color) {
		this.color = color;
		this.triangles = triangles;
	}

	public DrawableObject(List<Triangle> triangles, int color) {
		this.color = new float[] { Color.red(color) / 255f,
				Color.green(color) / 255f, Color.blue(color) / 255f,
				Color.alpha(color) / 255f };
		this.triangles = triangles;
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw(float[] mvpMatrix) {
		if (visible) {
			// Add program to OpenGL environment
			GLES20.glUseProgram(mProgram);
			MyGLRenderer.checkGlError("mProgram");

			// get handle to vertex shader's vPosition member
			mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
			MyGLRenderer.checkGlError("glGetAttribLocation");

			// Enable a handle to the triangle vertices
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			MyGLRenderer.checkGlError("glEnableVertexAttribArray");

			// Prepare the triangle coordinate data
			GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
					GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
			MyGLRenderer.checkGlError("glVertexAttribPointer");

			// get handle to fragment shader's vColor member
			mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

			// for alpha
			// GLES20.glEnable(GLES20.GL_BLEND);
			// GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
			// GLES20.GL_ONE_MINUS_SRC_ALPHA);

			// Prepare the triangle coordinate data
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);

			// get handle to shape's transformation matrix
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,
					"uMVPMatrix");
			MyGLRenderer.checkGlError("glGetUniformLocation");

			// Apply the projection and view transformation
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
			MyGLRenderer.checkGlError("glUniformMatrix4fv");

			// Draw the triangle
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
			MyGLRenderer.checkGlError("glDrawArrays");

			// Disable vertex array
			GLES20.glDisableVertexAttribArray(mPositionHandle);
		}
	}

	/**
	 * Concatenates a list of float arrays into a single array.
	 * 
	 * @param arrays
	 *            The arrays.
	 * @return The concatenated array.
	 * 
	 * @see {@link http
	 *      ://stackoverflow.com/questions/80476/how-to-concatenate-two
	 *      -arrays-in-java}
	 */
	public static float[] concatAllFloat(float[]... arrays) {
		if (arrays[0] == null) {
			return arrays[1]; // TODO
		}
		int totalLength = 0;
		final int subArrayCount = arrays.length;
		for (int i = 0; i < subArrayCount; ++i) {
			if (arrays[i] != null) {
				totalLength += arrays[i].length;
			}
		}
		float[] result = Arrays.copyOf(arrays[0], totalLength);
		int offset = arrays[0].length;
		for (int i = 1; i < subArrayCount; ++i) {
			System.arraycopy(arrays[i], 0, result, offset, arrays[i].length);
			offset += arrays[i].length;
		}
		return result;
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
		float[] vertices = null;
		for (Triangle triangle : triangles) {
			vertices = concatAllFloat(vertices, triangle.getTrianglesSorted());
		}
		triangleCoords = vertices;

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bv = ByteBuffer.allocateDirect(
		// (number of coordinate values * 4 bytes per float)
				triangleCoords.length * 4);
		// use the device hardware's native byte order
		bv.order(ByteOrder.nativeOrder());
		vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bv.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(this.triangleCoords);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);

		// prepare shaders and OpenGL program
		int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram); // create OpenGL program executables
	}

}
