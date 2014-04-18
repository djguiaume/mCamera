package com.epitech.mcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.media.ExifInterface;
import android.media.CamcorderProfile;
import android.media.FaceDetector;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.SurfaceHolder;

public class MCamera {
	private static String TAG = "MCamera";
	private static String mSaveDir = "MyCameraApp";
	private Camera mCamera = null;
	private Location location = null;
	private MediaRecorder mMediaRecorder;
	private boolean isRecording = false;
	private Context mContext = null;

	public MCamera() {

	}

	public void setLocation(Location loc) {
		location = loc;
	}

	public boolean init(Context context) {
		mContext = context;
		if (mCamera != null) {
			Log.d(TAG, "already init.");
			return false;
		}
		if (checkCameraHardware(context) == false)
			return false;
		mCamera = getCameraInstance();

        mCamera.setFaceDetectionListener (new OverlayView(context));
        mCamera.startFaceDetection();
        Log.d(MySurfaceView.VTAG, "Face detection started");

		if (mCamera == null)
			return false;
		return true;
	}

	public void destroy() {
		Log.d(TAG, "destroy called.");
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder == null)
			return;
		mMediaRecorder.reset();
		mMediaRecorder.release();
		mMediaRecorder = null;
		mCamera.lock();
	}

	public Camera getCamera() {
		if (mCamera == null)
			Log.w(TAG, "getCamera called without init.");
		return mCamera;
	}

	private void SetExifGPSData(File fn) {
		if (location == null)
			return;
		ExifInterface exif;
		double glat = location.getLatitude();
		double glong = location.getLongitude();

		Log.d(MySurfaceView.VTAG, "setting exif data lat : " + glat
                + " long : " + glong);

		int num1Lat = (int) Math.floor(glat);
		int num2Lat = (int) Math.floor((glat - num1Lat) * 60);
		double num3Lat = (glat - ((double) num1Lat + ((double) num2Lat / 60))) * 3600000;

		int num1Lon = (int) Math.floor(glong);
		int num2Lon = (int) Math.floor((glong - num1Lon) * 60);
		double num3Lon = (glong - ((double) num1Lon + ((double) num2Lon / 60))) * 3600000;

		try {
			exif = new ExifInterface(fn.getAbsolutePath());
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat + "/1,"
					+ num2Lat + "/1," + num3Lat + "/1000");
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon + "/1,"
					+ num2Lon + "/1," + num3Lon + "/1000");
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
					(glat > 0) ? "N" : "S");
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
					(glong > 0) ? "E" : "W");
			exif.saveAttributes();
		} catch (IOException e) {
			Log.d(MySurfaceView.VTAG, "Error set exif");
		}

	}

	public static Camera getCameraInstance() {

		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return c;
	}

	private static File getOutputMediaFile(int type) throws Exception {

		if (!(Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())))
			throw new Exception("SDCard not properly mounted.");

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				mSaveDir);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs())
				throw new Exception("failed to create directory " + mSaveDir);
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
			throw new Exception("Unknown media file type");
		}

		return mediaFile;
	}

	private boolean checkCameraHardware(Context context) {
		Log.d(TAG, "CheckCameraHardware mPicture=" + mPicture + "mCamera="
				+ mCamera);
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA))
			return true;
		else
			return false;
	}

	public void takePicture() {
		TakePictureTask takePictureTask = new TakePictureTask();
		takePictureTask.execute();
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken mPicture=" + mPicture + "mCamera="
					+ mCamera);
			File pictureFile = null;
			try {
				pictureFile = getOutputMediaFile(FileColumns.MEDIA_TYPE_IMAGE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage());
			}
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
			
			ContentValues image = new ContentValues();

			image.put(Images.Media.TITLE, pictureFile.getName());
			image.put(Images.Media.DISPLAY_NAME, pictureFile.getName());
			image.put(Images.Media.MIME_TYPE, "image/jpg");
			if (location != null) {
				image.put(Images.Media.LATITUDE, location.getLatitude());
				image.put(Images.Media.LONGITUDE, location.getLongitude());
			}
			image.put(Images.Media.ORIENTATION, 0);

			File parent = pictureFile.getParentFile();
			String path = parent.toString().toLowerCase();
			String name = parent.getName().toLowerCase();
			image.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
			image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
			image.put(Images.Media.SIZE, pictureFile.length());

			image.put(Images.Media.DATA, pictureFile.getAbsolutePath());

			mContext.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
		}
	};

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



	private boolean prepareVideoRecorder(SurfaceHolder holder) {

		// mCamera = getCameraInstance();
		Log.d("VIDEO", "mCamera =" + mCamera);
		if (mCamera == null)
			return false;
		mMediaRecorder = new MediaRecorder();
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));

		try {
			mMediaRecorder.setOutputFile(getOutputMediaFile(
					FileColumns.MEDIA_TYPE_VIDEO).toString());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}

		// TODO: Do in MySurfaceView?
		mMediaRecorder.setPreviewDisplay(holder.getSurface());

		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d(TAG,
					"IllegalStateException preparing MediaRecorder: "
							+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	public boolean startVideoRecording(SurfaceHolder holder) {
		if (prepareVideoRecorder(holder)) {
			try {
				mMediaRecorder.start();
				isRecording = true;
			} catch (IllegalStateException e) {
				Log.d(TAG,
						"IllegalStateException starting MediaRecorder: "
								+ e.getMessage());
				releaseMediaRecorder();
				return false;
			}
		} else {
			releaseMediaRecorder();
			return false;
		}
		return true;

	}

	public void stoptVideoRecording() {
		mMediaRecorder.stop();
		releaseMediaRecorder();
		mCamera.lock();
		isRecording = false;
	}

	public boolean isRecording() {
		return isRecording;
	}
}
