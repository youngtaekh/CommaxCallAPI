package io.dotconnect.p2p.wwc;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import androidx.annotation.Nullable;
import org.webrtc.*;

public class Camera {
    private static final String TAG = "Video";
    private Context mContext;

    public Camera(Context mContext) {
        this.mContext = mContext;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(mContext);
    }

    private @Nullable
    VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    public @Nullable VideoCapturer createVideoCapturer() {
        final VideoCapturer videoCapturer;
        if (useCamera2()) {
            Logging.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(mContext));
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(false));
        }
        if (videoCapturer == null) {
//            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    @TargetApi(21)
    public @Nullable VideoCapturer createScreenCapturer(Intent mediaProjectionPermissionResultData) {
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
            }
        });
    }
}
