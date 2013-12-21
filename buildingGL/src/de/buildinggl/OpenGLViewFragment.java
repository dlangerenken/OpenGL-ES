package de.buildinggl;

import melb.mSafe.model.Model3D;
import melb.mSafe.model.RouteGraph;
import melb.mSafe.model.Vector3D;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.gson.Gson;

import de.buildinggl.drawable.Layer3DGL;
import de.buildinggl.drawable.Model3DGL;
import de.buildinggl.utilities.Helper;
import de.buildinggl.views.CustomSeekBar;

public class OpenGLViewFragment extends Fragment implements IUserPosition {

	public static final int maxZoomLevel = 20;
	private MyGLSurfaceView mGLView;
	private MyGLRenderer mGLRenderer;
	private Model3DGL model3d;
	private CustomSeekBar seekBarX;
	private LinearLayout listOfFloors;
	private RouteGraph routeGraph;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout rootView = (FrameLayout) inflater.inflate(R.layout.gl_view,
				null);
		if (hasGLES20()) {
			mGLView = new MyGLSurfaceView(getActivity(), model3d);
			rootView.addView(mGLView, 0);
		}
		listOfFloors = (LinearLayout) rootView.findViewById(R.id.listOfFloors);
		initListView();
		initSeekBars(rootView);
		return rootView;
	}

	private void initListView() {
		int i = 0;
		for (final Layer3DGL layer : model3d.getLayers()) {
			Button button = new Button(getActivity());
			button.setText(i++ + "");
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					layer.setVisible(!layer.isVisible());
				}
			});
			listOfFloors.addView(button);
		}
	}

	private void initSeekBars(FrameLayout rootView) {
		seekBarX = (CustomSeekBar) rootView.findViewById(R.id.seekBarX);
		if (mGLView != null) {
			mGLRenderer = mGLView.getRenderer();

			seekBarX.setMin(0);
			seekBarX.setMax(80);
			seekBarX.setProgress((int) (mGLRenderer.getRotationX() - mGLRenderer
					.getDefaultRotationX()));
			seekBarX.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mGLRenderer.setRotationX((float) progress);

				}
			});
		}
	}

	private void initialize() {
		try {
			Model3D model = new Gson().fromJson(Helper.getStringFromRaw(
					getActivity(), R.raw.building_model), Model3D.class);
			model3d = new Model3DGL(model);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean hasGLES20() {
		ActivityManager am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return info.reqGlEsVersion >= 0x20000;
	}

	@Override
	public void onPause() {
		super.onPause();
		/*
		 * The following call pauses the rendering thread. If your OpenGL
		 * application is memory intensive, you should consider de-allocating
		 * objects that consume significant memory here.
		 */
		mGLView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * The following call resumes a paused rendering thread. If you
		 * de-allocated graphic objects for onPause() this is a good place to
		 * re-allocate them.
		 */
		mGLView.onResume();
	}

	public void downPressed() {
		mGLRenderer.downPressed();
	}

	public void upPressed() {
		mGLRenderer.upPressed();
	}

	@Override
	public void userPositionChanged(Vector3D userPosition) {
		model3d.setUserPosition(userPosition);
	}
}
