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
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;
import android.view.SurfaceHolder;

public class MCamera {
	private static String TAG = "MCamera";
	private static String mSaveDir = "MyCameraApp";
	private Camera mCamera = null;
	private MediaRecorder mMediaRecorder;

	public MCamera() {

	}

	public boolean init(Context context) {
		if (mCamera != null) {
			Log.d(TAG, "already init.");
			return false;
		}
		if (checkCameraHardware(context) == false)
			return false;
		mCamera = getCameraInstance();
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

		if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
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
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
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

		mCamera = getCameraInstance();
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
		if (prepareVideoRecorder(holder))
			mMediaRecorder.start();
		else {
			releaseMediaRecorder();
			return false;
		}
		return true;

	}

	public void stoptVideoRecording() {
		mMediaRecorder.stop();
		releaseMediaRecorder();
		mCamera.lock();
	}
}
