package com.epitech.mcamera;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.os.Build;

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

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			Log.d("toto", "ON CREATE VIEW");

			// Create our Preview view and set it as the content of our
			// activity.
			mPreview = new MySurfaceView(getActivity());
			FrameLayout preview = (FrameLayout) rootView
					.findViewById(R.id.camera_preview);
			preview.addView(mPreview);

			Button captureButton = (Button) rootView
					.findViewById(R.id.button_capture);
			captureButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an image from the camera
					// mPreview.stopPreview();
					Log.d(ACCOUNT_SERVICE, "onClick");
					mPreview.takePicture();
					// mPreview.stopPreview();
				}
			});
			// mPreview.startPreview();
			return rootView;
		}

		@Override
		public void onDestroyView() {
			super.onDestroyView();
			if (mPreview != null) {
				Log.d("toto", "ONDESTROY VIEW");
				mPreview.destroyPreview();
				mPreview = null;
				FrameLayout preview = (FrameLayout) rootView
						.findViewById(R.id.camera_preview);
				preview.removeView(mPreview);
			}
		}

		@Override
		public void onDestroy() { 
			super.onDestroy();

			if (mPreview != null) {
				Log.d("toto", "ONDESTROY");
				mPreview.destroyPreview();
				mPreview = null;
				FrameLayout preview = (FrameLayout) rootView
						.findViewById(R.id.camera_preview);
				preview.removeView(mPreview);
			}
			// mCamera = null;
		}

		@Override
		public void onPause() {
			super.onPause();
			Log.d("toto", "ONPAUSE");
			mPreview.destroyPreview();
			mPreview = null;
			FrameLayout preview = (FrameLayout) rootView
					.findViewById(R.id.camera_preview);
			preview.removeView(mPreview);
			// mCamera = null;
		}

		@Override
		public void onResume() {
			Log.d("toto", "ON RESUME VIEW");
			super.onResume();
			if (mPreview != null) {
				//mPreview.startPreview();
				return;
			}
			mPreview = new MySurfaceView(getActivity());
			FrameLayout preview = (FrameLayout) rootView
					.findViewById(R.id.camera_preview);
			preview.addView(mPreview);
			 mPreview.startPreview();
		}
	}

}
