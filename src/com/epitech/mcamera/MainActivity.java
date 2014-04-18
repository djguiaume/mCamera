package com.epitech.mcamera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * WindowManager.LayoutParams attrs = this.getWindow().getAttributes();
		 * attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		 * this.getWindow().setAttributes(attrs);
		 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
		if (id == R.id.settings) {
			showUserSettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showUserSettings() {
		startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private MySurfaceView mPreview = null;
		private View rootView;
		private static String TAG = "PlaceholderFragment";

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			Log.d(TAG, "ON CREATE VIEW");

			// Create our Preview view and set it as the content of our
			// activity.
			

			Button photoButton = (Button) rootView
					.findViewById(R.id.button_photo);
			photoButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an image from the camera
					Log.d(ACCOUNT_SERVICE, "onClick");
					mPreview.takePicture();
				}
			});

			Button videoButton = (Button) rootView
					.findViewById(R.id.button_video);
			videoButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an image from the camera
					Log.d(ACCOUNT_SERVICE, "onClick VIDEO");
					mPreview.takeVideo();
				}
			});

			// mPreview.startPreview();
			return rootView;
		}

		private void setFeatureControls(String featureName, View rootView) {
			if (featureName == MySurfaceView.ZOOM_FEATURE_NAME) {
				Button zoomPlus = (Button) rootView
						.findViewById(R.id.button_zoom_plus);
				Button zoomMinus = (Button) rootView
						.findViewById(R.id.button_zoom_minus);
				zoomMinus.setOnClickListener(new OnZoomButtonPushedListerner());
				zoomPlus.setOnClickListener(new OnZoomButtonPushedListerner());
				zoomMinus.setVisibility(View.VISIBLE);
				zoomPlus.setVisibility(View.VISIBLE);
			}
		}

		public class OnZoomButtonPushedListerner implements OnClickListener {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.button_zoom_plus:

					break;
				case R.id.button_zoom_minus:

					break;
				}
			}
		}

		@Override
		public void onResume() {
			Log.d(TAG, "ON RESUME VIEW");
			super.onResume();
			
			mPreview = new MySurfaceView(getActivity());
			FrameLayout preview = (FrameLayout) rootView
					.findViewById(R.id.camera_preview);
			preview.addView(mPreview);
			mPreview.startPreview();
			
			Log.v(TAG, "Checks feature");
			if (mPreview.hasFeature(MySurfaceView.ZOOM_FEATURE_NAME)) {
				Log.v(TAG, "On a la feature");
				setFeatureControls(MySurfaceView.ZOOM_FEATURE_NAME, rootView);
			} else {
				Log.v(TAG, "On a pas la feature");
				
			}
		}

		@Override
		public void onPause() {
			super.onPause();
			Log.d(TAG, "ONPAUSE");
		
			FrameLayout preview = (FrameLayout) rootView
					.findViewById(R.id.camera_preview);
			preview.removeView(mPreview);
			mPreview.destroyPreview();
			mPreview = null;
		}

	
	}

}
