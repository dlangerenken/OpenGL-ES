package de.buildinggl;

import melb.mSafe.model.Vector3D;
import android.opengl.Matrix;

public class Camera {

	private float mDistance;
	private Vector3D mRotationVector;
	private Vector3D mLookAtVector;
	private float[] mModelMatrix;
	private boolean recalculate = true;
	private int offset = 0;
	private float[] lookingTo;

	public Camera() {
		mDistance = 2;
		mRotationVector = new Vector3D(0, 0, 0);
		mLookAtVector = new Vector3D(0, 0, 0);
		mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
	}

	public float[] getViewMatrix() {
		if (recalculate) {
			lookingTo = new float[16];
			/*
			 * Set the camera position (View matrix)
			 */
			float[] currentTarget = new float[] { mLookAtVector.getX(),
					mLookAtVector.getY(), mLookAtVector.getZ(), 1 };
			float[] targetVector = new float[4];
			Matrix.multiplyMV(targetVector, 0, mModelMatrix, 0, currentTarget,
					0);
			Vector3D transformedTarget = new Vector3D(targetVector[0],
					targetVector[1], targetVector[2]);
			lookingTo = getLookAtM(transformedTarget);
		}
		return lookingTo;
	}

	private float[] getLookAtM(Vector3D lookAtTarget) {
		float[] lookingTo = new float[16];

		double x = mDistance
				* Math.sin(Math.toRadians(mRotationVector.getZ())
						* Math.abs(Math.toRadians(Math.cos(mRotationVector
								.getX()))));
		double y = mDistance * Math.sin(Math.toRadians(mRotationVector.getX()));
		double z = mDistance
				* Math.cos(Math.toRadians(mRotationVector.getZ())
						* Math.abs(Math.toRadians(Math.cos(mRotationVector
								.getX()))));

		Vector3D dist = new Vector3D((float) x, (float) y, (float) z);
		Vector3D eyePt = Vector3D.subtract(lookAtTarget, dist);
		Vector3D lookAt = lookAtTarget;
		Vector3D up = new Vector3D(0.0f, 1.0f, 0.0f);

		Matrix.setLookAtM(lookingTo, offset, eyePt.getX(), eyePt.getY(),
				eyePt.getZ(), lookAt.getX(), lookAt.getY(), lookAt.getZ(),
				up.getX(), up.getY(), up.getZ());

		recalculate = false;
		return lookingTo;
	}

	public void setLookAtPosition(Vector3D lookAtVector) {
		mLookAtVector = lookAtVector;
		recalculate = true;
	}

	public void setDistance(float distance) {
		mDistance = distance;
	}

	public void setRotation(Vector3D rotationVector) {
		mRotationVector = rotationVector;
		recalculate = true;
	}

	public void setModelMatrix(float[] modelMatrix) {
		mModelMatrix = modelMatrix;
		recalculate = true;
	}
}
