package io.dotconnect.api.view;

import android.content.Context;
import android.util.AttributeSet;
import org.webrtc.SurfaceViewRenderer;

public class ConnectView extends SurfaceViewRenderer {
    public ConnectView(Context context) {
        super(context);
    }

    public ConnectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
