package com.epitech.mcamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Created by vayan on 4/18/14.
 */
public class InYourFaceListen implements Camera.FaceDetectionListener {
    Context ctx;
    MCamera cam;
    RelativeLayout ly;

    public InYourFaceListen(RelativeLayout relativeLayout) {
        ly = relativeLayout;
    }

    public InYourFaceListen(Context context, RelativeLayout relativeLayout) {
       ctx = context;
       ly = relativeLayout;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        ly.removeAllViews();
        if (faces.length > 0) {
            OverlayView ov = new OverlayView(ctx, faces, 1920, 1080);
            ov.setFaces(faces);
            ly.addView(ov);
        }
    }
}
