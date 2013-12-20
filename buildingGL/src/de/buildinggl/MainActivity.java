package de.buildinggl;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	private OpenGLViewFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragment = new OpenGLViewFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content, fragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			fragment.downPressed();
			Toast.makeText(this, "down", Toast.LENGTH_SHORT).show();
		}
		if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
			fragment.upPressed();
			Toast.makeText(this, "up", Toast.LENGTH_SHORT).show();
		}
		return true;
	}

}
