package com.epitech.mcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;

public class MCamera {
	private static String TAG = "mCamera";
	private Camera mCamera = null;
    private Location location = null;

	public MCamera() {

	}

    public void setLocation(Location loc) {
        location = loc;
    }

	public boolean init(Context context) {
		Log.d(TAG, "init mPicture="+mPicture+"mCamera="+mCamera);
		if (mCamera != null)
			return false;
		if (checkCameraHardware(context) == false)
			return false;
		mCamera = getCameraInstance();
		if (mCamera == null)
			return false;
		return true;
	}

	public void destroy() {
		Log.d(TAG, "destroy mPicture="+mPicture+"mCamera="+mCamera);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	public Camera getCamera() {
		Log.d(TAG, "getCamera mPicture="+mPicture+"mCamera="+mCamera);
		return mCamera;
	}

	public void takePicture() {

		TakePictureTask takePictureTask = new TakePictureTask();
		takePictureTask.execute();
		Log.d(TAG, "takePicture mPicture="+mPicture+"mCamera="+mCamera);
		//mCamera.takePicture(null, null, mPicture);
	}

	private boolean checkCameraHardware(Context context) {
		Log.d(TAG, "CheckCameraHardware mPicture="+mPicture+"mCamera="+mCamera);
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA))
			return true;
		else
			return false;
	}

    private void SetExifGPSData(File fn) {
        if (location == null)
                return;
        ExifInterface exif;
        double glat = location.getLatitude();
        double glong = location.getLongitude();

        Log.d(mySurfaceView.VTAG, "setting exif data lat : " + glat + " long : "  + glong);

        int num1Lat = (int)Math.floor(glat);
        int num2Lat = (int)Math.floor((glat - num1Lat) * 60);
        double num3Lat = (glat - ((double)num1Lat+((double)num2Lat/60))) * 3600000;

        int num1Lon = (int)Math.floor(glong);
        int num2Lon = (int)Math.floor((glong - num1Lon) * 60);
        double num3Lon = (glong - ((double)num1Lon+((double)num2Lon/60))) * 3600000;

        try {
            exif = new ExifInterface(fn.getAbsolutePath());
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, (glat > 0) ? "N" : "S");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, (glong > 0) ? "E" : "W");
            exif.saveAttributes();
        } catch (IOException e){
            Log.d(mySurfaceView.VTAG, "Error set exif");
        }

    }

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken mPicture="+mPicture+"mCamera="+mCamera);
			File pictureFile = getOutputMediaFile(FileColumns.MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions.");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				Log.d(TAG, "fos=" + fos);
				Log.d(TAG, "data=" + data);
				fos.write(data);
				fos.close();
                SetExifGPSData(pictureFile);
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}
	};

	public static Camera getCameraInstance() {
		
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		Log.d(TAG, "getCameraInstance C="+c);
		return c; // returns nu if camera is unavailable
	}

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == FileColumns.MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == FileColumns.MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	private class TakePictureTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			mCamera.startPreview();
		}

		@Override
		protected Void doInBackground(Void... params) {
			mCamera.takePicture(null, null, mPicture);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
