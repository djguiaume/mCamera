package com.epitech.mcamera;

import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.epitech.mcamera.MainActivity.PlaceholderFragment.plugin;

public class ZoomPlugin implements plugin {
	MySurfaceView mPreview;
	Camera mCamera;
	View mRootView;
	private boolean isSmoothZoomAvalaible = false;
	private boolean isSmoothZooming = false;
	private String TAG = "ZoomPlugin";

	@Override
	public void askFeature(MySurfaceView preview, View rootview, Camera camera) {
		mPreview = preview;
		mCamera = camera;
		mRootView = rootview;

		Log.v(TAG, "Checks feature");
		if (mPreview.hasFeature(MySurfaceView.ZOOM_FEATURE_NAME)) {
			Log.v(TAG, "On a la feature");
			setFeatureControls(mRootView);
		} else {
			Log.v(TAG, "On a pas la feature");
		}
	}

	private void setFeatureControls(View rootView) {
		Button zoomPlus = (Button) rootView.findViewById(R.id.button_zoom_plus);
		Button zoomMinus = (Button) rootView
				.findViewById(R.id.button_zoom_minus);
		zoomMinus.setOnClickListener(new OnZoomButtonPushedListerner());
		zoomPlus.setOnClickListener(new OnZoomButtonPushedListerner());
		zoomMinus.setVisibility(View.VISIBLE);
		zoomPlus.setVisibility(View.VISIBLE);
		if (mPreview.hasFeature(MySurfaceView.SMOOTHZOOM_FEATURE_NAME)) {
			isSmoothZoomAvalaible = true;
		} else {
			isSmoothZoomAvalaible = false;
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

}