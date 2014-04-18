package com.epitech.mcamera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class mySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "SURFACE";
	private SurfaceHolder mHolder;
	private MCamera mCamera;
	private Context mContext;

	public mySurfaceView(Context context) {
		super(context);
		mContext = context;
		getHolder().addCallback(this);
		Log.d(TAG, "surfaceView Constructor"); 
		mCamera = new MCamera();
		if (!mCamera.init(context)) {
			Log.e("onCreateView", "mCamera init failed (no camera?)");
			// TODO: Show a message to user and quit?
		}
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w("surfaceCreated", "On Surface Created");
		mHolder = holder;
		startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		Log.d(TAG, "surfaceDestroy");
		//mCamera.destroy();
		//mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d(TAG, "surfaceCHANGED");
		if (holder.getSurface() == null) {
			Log.d(TAG, "surfaceCHANGED m null");
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		/*try {
			
			mCamera.getCamera().stopPreview();
		} catch (Exception e) {
			
			// ignore: tried to stop a non-existent preview
		}*/
		
		mCamera.destroy();
		if (!mCamera.init(mContext)) {
			Log.e("onCreateView", "mCamera init failed (no camera?)");
			// TODO: Show a message to user and quit?
		}
		
		
		
		Log.d(TAG, "mHolder = "+mHolder+" holder = "+holder);
		mHolder = holder;
		try {
			mCamera.getCamera().getParameters().setPreviewSize(w, h);
			mCamera.getCamera().getParameters().setPreviewFormat(format);
			mCamera.getCamera().setPreviewDisplay(mHolder);
			mCamera.getCamera().startPreview();
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	public void startPreview() {
		getHolder().addCallback(this);
		try {
			mCamera.getCamera().setPreviewDisplay(mHolder);
			Log.d(TAG, "mCamera=" + mCamera);
			mCamera.getCamera().startPreview();
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void stopPreview() {
		try {
			mCamera.getCamera().stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
	}
	
	public void destroyPreview() {
		Log.d(TAG, "DESTROY PREVIEW mCamera=" + mCamera);
		mCamera.getCamera().setPreviewCallback(null);
		mCamera.destroy();
	}
	
	public void takePicture() {
		mCamera.takePicture();
	}
}