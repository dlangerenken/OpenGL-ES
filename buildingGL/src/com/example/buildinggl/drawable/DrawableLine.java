package com.example.buildinggl.drawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import android.graphics.Color;
import android.opengl.GLES20;

import com.example.buildinggl.MyGLRenderer;
import com.example.buildinggl.Point;

public class DrawableLine implements IDrawableObject {

	private final String vertexShaderCode =
	// This matrix member variable provides a hook to manipulate
	// the coordinates of the objects that use this vertex shader
	"uniform mat4 uMVPMatrix;" + "attribute vec4 vPosition;" + "void main() {" +
	// the matrix must be included as a modifier of gl_Position
	// Note that the uMVPMatrix factor *must be first* in order
	// for the matrix multiplication product to be correct.
			"  gl_Position = uMVPMatrix * vPosition;}";

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
	private int vertexCount;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
															// coordinate

	private float[] color;

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param color
	 */
	public DrawableLine(List<Point> points, float[] color) {
		init(points, color);
	}

	private void init(List<Point> points, float[] color) {
		float[] vertices = new float[points.size() * 3];
		this.color = color;
		int counter = 0;
		for (int i = 0; i < points.size(); i++) {
			float[] pointFloat = points.get(i).getXYZ();
			vertices[counter++] = pointFloat[0];
			vertices[counter++] = pointFloat[1];
			vertices[counter++] = pointFloat[2];
		}

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bv = ByteBuffer.allocateDirect(
		// (number of coordinate values * 4 bytes per float)
				vertices.length * 4);
		// use the device hardware's native byte order
		bv.order(ByteOrder.nativeOrder());
		vertexCount = vertices.length / COORDS_PER_VERTEX;

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bv.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(vertices);
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

	public DrawableLine(List<Point> points, int color) {
		init(points, new float[] { Color.red(color) / 255f,
				Color.green(color) / 255f, Color.blue(color) / 255f, 1.0f });
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		
		GLES20.glLineWidth(5.0f);
		// Prepare the triangle coordinate data
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		MyGLRenderer.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		MyGLRenderer.checkGlError("glUniformMatrix4fv");

		// Draw the triangle
		GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

}
