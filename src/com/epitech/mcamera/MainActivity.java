package com.epitech.mcamera;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.os.Build;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		private mySurfaceView mPreview;
		private MCamera mCamera;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			mCamera = new MCamera();
			if (!mCamera.init(getActivity())) {
				Log.e("onCreateView", "mCamera init failed (no camera?)");
				//TODO: Show a message to user and quit?
				return rootView;
			}
			
			// Create our Preview view and set it as the content of our activity.
			mPreview = new mySurfaceView(getActivity(), mCamera.getCamera());
			FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
			preview.addView(mPreview);
			Button captureButton = (Button) rootView.findViewById(R.id.button_capture);
			captureButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// get an image from the camera
						//mPreview.stopPreview();
						mCamera.takePicture();
						mPreview.startPreview();
					}
				}
			);
			return rootView;
		}
	}

}
