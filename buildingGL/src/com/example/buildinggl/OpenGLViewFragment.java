package com.example.buildinggl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import melb.mSafe.model.Model3D;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
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

import com.example.buildinggl.drawable.Layer3DGL;
import com.example.buildinggl.drawable.Model3DGL;
import com.google.gson.Gson;

public class OpenGLViewFragment extends Fragment {

	public static final int maxZoomLevel = 20;
	private MyGLSurfaceView mGLView;
	private MyGLRenderer mGLRenderer;

	private Model3DGL model3d;
	private CustomSeekBar seekBarZ;
	private CustomSeekBar seekBarX;
	private LinearLayout listOfFloors;

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
		seekBarZ = (CustomSeekBar) rootView.findViewById(R.id.seekBarZ);
		seekBarZ.setVisibility(View.GONE); // TODO
		seekBarX = (CustomSeekBar) rootView.findViewById(R.id.seekBarX);
		if (mGLView != null) {
			mGLRenderer = mGLView.getRenderer();

			seekBarZ.setMin(00);
			seekBarZ.setMax(360);
			seekBarZ.setProgress((int) mGLRenderer.getRotationZ());
			seekBarZ.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mGLRenderer.setRotationZ((float) progress);
				}
			});

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	private void initialize() {
		try {
			// /*
			// * simple layer for testing
			// */

			Model3D model = new Gson().fromJson(
					getStringFromRaw(getActivity(), R.raw.building_model),
					Model3D.class);
//			model.length = 1948; // TODO in xml
//			model.width = 1169; // TODO in xml
//			model.height = 247;
			model3d = new Model3DGL(model);
			// Layer3D layer = model3d.layers.get(0);
			// model3d.layers = new ArrayList<Layer3D>();
			// model3d.layers.add(layer);

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
		// The following call pauses the rendering thread.
		// If your OpenGL application is memory intensive,
		// you should consider de-allocating objects that
		// consume significant memory here.
		mGLView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		// The following call resumes a paused rendering thread.
		// If you de-allocated graphic objects for onPause()
		// this is a good place to re-allocate them.
		mGLView.onResume();
	}

	private String getStringFromRaw(Context c, int raw) throws IOException {
		Resources r = c.getResources();
		InputStream is = r.openRawResource(raw);
		String statesText = convertStreamToString(is);
		is.close();
		return statesText;
	}

	private String convertStreamToString(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = is.read();
		while (i != -1) {
			baos.write(i);
			i = is.read();
		}
		return baos.toString();
	}

	public void downPressed() {
		mGLRenderer.downPressed();
	}

	public void upPressed() {
		mGLRenderer.upPressed();
	}
}
