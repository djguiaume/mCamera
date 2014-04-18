package com.epitech.mcamera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class mySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "SURFACE";
    public static final String VTAG = "VAYATAG";
	private SurfaceHolder mHolder;
	private MCamera mCamera;
	private Context mContext;
    LocationManager locationManager;
    LocationListener locationListener;


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
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(mySurfaceView.VTAG, "new location");
                mCamera.setLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { Log.d(mySurfaceView.VTAG, "Status Changed"); }

            public void onProviderEnabled(String provider) { Log.d(mySurfaceView.VTAG, "Provider enable"); }

            public void onProviderDisabled(String provider) { Log.d(mySurfaceView.VTAG, "Provider disable"); }
        };
        Log.d(mySurfaceView.VTAG, "Start find provider ");
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            Log.d(mySurfaceView.VTAG, "NETWORK_PROVIDER");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        else if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Log.d(mySurfaceView.VTAG, "GPS_PROVIDER");
        }
        else if (locationManager.getAllProviders().contains(LocationManager.PASSIVE_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
            Log.d(mySurfaceView.VTAG, "PASSIVE_PROVIDER");
        }
        else Log.d(mySurfaceView.VTAG, "No fuckin provider");
        startPreview();
    }

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		Log.d(TAG, "surfaceDestroy");
        locationManager.removeUpdates(locationListener);
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