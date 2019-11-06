package io.dotconnect.p2p;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.dotconnect.p2p.wwc.Camera;
import io.dotconnect.p2p.wwc.Ice;
import org.webrtc.*;

import java.util.ArrayList;
import java.util.List;

import static io.dotconnect.api.util.Configuration.APP_NAME;
import static io.dotconnect.p2p.utils.Configuration.*;

public class PeerConnectionManager implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents {
    private static final String TAG = "PeerConnectionManager";

    @Nullable
    private SurfaceViewRenderer pipRenderer;
    @Nullable
    private SurfaceViewRenderer fullscreenRenderer;

    private Handler handler = null;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendSdp();
        }
    };

    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }

    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private final ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private final List<VideoSink> remoteSinks = new ArrayList<>();
    @Nullable private PeerConnectionClient peerConnectionClient;
    @Nullable
    private AppRTCClient.SignalingParameters signalingParameters;
    private SessionDescription answerSdp=null;
    private SDPListener listener;

    @Nullable
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private Context context;

    private static PeerConnectionManager instance;

    public static PeerConnectionManager getInstance() {
        return instance;
    }

    public static PeerConnectionManager getInstance(Context context, boolean isDataChannel,
                                                    boolean isVideoCall, boolean isScreenCall, Intent data) {
        if (instance == null)
            instance = new PeerConnectionManager(context, isDataChannel, isVideoCall, isScreenCall, false, data);
        return instance;
    }

    public static PeerConnectionManager getInstance(Context context, boolean isDataChannel, boolean isVideoCall) {
        if (instance == null)
            instance = new PeerConnectionManager(context, isDataChannel, isVideoCall, false, false, null);
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    private PeerConnectionManager(Context context, boolean isDataChannel,
                                  boolean isVideoCall, boolean isScreenCall, boolean videoRecvOnly, Intent data) {
        this.context = context;
        remoteSinks.add(remoteProxyRenderer);

        final EglBase eglBase = EglBase.create();

        PeerConnectionClient.DataChannelParameters
                dataChannelParameters = null;
        if (isDataChannel) {
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters(ORDERED, MAX_RETRANSMIT_TIME_MS,
                    MAX_RETRANSMITS, PROTOCOL, NEGOTIATED, ID);
        }
        peerConnectionParameters =
                new PeerConnectionClient.PeerConnectionParameters(isVideoCall, isScreenCall, videoRecvOnly, false,
                        TRACING, VIDEO_WIDTH, VIDEO_HEIGHT, VIDEO_FPS,
                        VIDEO_MAX_BITRATE, VIDEO_CODEC,
                        VIDEO_CODEC_HW_ACCELERATION,
                        VIDEO_FLEXFEC_ENABLED,
                        AUDIO_START_BITRATE, AUDIO_CODEC,
                        NO_AUDIO_PROCESSING,
                        AEC_DUMP,
                        SAVE_INPUT_AUDIO_TO_FILE,
                        USE_OPEN_SLES,
                        DISABLE_BUILT_IN_AEC,
                        DISABLE_BUILT_IN_AGC,
                        DISABLE_BUILT_IN_NS,
                        DISABLE_WEBRTC_AGC_AND_HPF,
                        ENABLE_RTC_EVENT_LOG, data, dataChannelParameters);

        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient(
                context, eglBase, peerConnectionParameters, this);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        options.networkIgnoreMask = 0;

        peerConnectionClient.createPeerConnectionFactory(options);
    }

    public void getSDP(boolean initiator, String remoteSDP, SDPListener listener) {
        this.listener = listener;
        // Request TURN servers.
        List<PeerConnection.IceServer> turnServers =
                new Ice().getIceServers();
        List<PeerConnection.IceServer> iceServers = new ArrayList<>(turnServers);

        SessionDescription offerSdp=null;
        if (remoteSDP!=null)
            offerSdp = new SessionDescription(SessionDescription.Type.OFFER, remoteSDP);
        AppRTCClient.SignalingParameters params = new AppRTCClient.SignalingParameters(
                iceServers, initiator, offerSdp, null);
        createPeerConnection(params);
    }

    public void hangupCall() {
        handler = null;
        remoteProxyRenderer.setTarget(null);
        localProxyVideoSink.setTarget(null);
        if (pipRenderer != null) {
            pipRenderer.release();
            pipRenderer = null;
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        clear();
    }

    private void createPeerConnection(@NonNull final AppRTCClient.SignalingParameters params) {
        signalingParameters = params;

        if (peerConnectionParameters==null || peerConnectionClient==null)
            return;

        if (peerConnectionParameters.screenCallEnabled) {
            peerConnectionClient.createPeerConnection(localProxyVideoSink, remoteSinks,
                    new Camera(context).createScreenCapturer(peerConnectionParameters.data), signalingParameters);
        } else {
            if (peerConnectionParameters.videoCallEnabled) {
                peerConnectionClient.createPeerConnection(
                        localProxyVideoSink, remoteSinks, new Camera(context).createVideoCapturer(), signalingParameters);
            } else {
                peerConnectionClient.createPeerConnection(
                        localProxyVideoSink, remoteSinks, null, signalingParameters);
            }
        }

        if (signalingParameters.initiator) {
//            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
//                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    private void sendSdp() {
        if (signalingParameters!=null && signalingParameters.offerSdp!=null) {
            if (signalingParameters.initiator) {
                this.listener.onLocalDescription(signalingParameters.offerSdp.description);
//                VantaProtocol vp = vantaCall.offer(signalingParameters.offerSdp.description, peerConnectionParameters.videoCallEnabled);
//                Vanta.getInstance().addVP(vp);
            } else {
                this.listener.onLocalDescription(answerSdp.description);
//                VantaProtocol vp = vantaCall.answer(answerSdp.description, peerConnectionParameters.videoCallEnabled);
//                Vanta.getInstance().addVP(vp);
            }
        }
    }

    public void setRenderer(@Nullable SurfaceViewRenderer fullscreenRenderer, @Nullable SurfaceViewRenderer pipRenderer) {
        this.fullscreenRenderer = fullscreenRenderer;
        this.pipRenderer = pipRenderer;

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final EglBase eglBase = EglBase.create();
                PeerConnectionManager.this.pipRenderer.init(eglBase.getEglBaseContext(), null);
                PeerConnectionManager.this.pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                PeerConnectionManager.this.fullscreenRenderer.init(eglBase.getEglBaseContext(), null);
                PeerConnectionManager.this.fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

                PeerConnectionManager.this.pipRenderer.setZOrderMediaOverlay(true);
                PeerConnectionManager.this.pipRenderer.setEnableHardwareScaler(true /* enabled */);
                PeerConnectionManager.this.fullscreenRenderer.setEnableHardwareScaler(false /* enabled */);

                setSwappedFeeds(true);
            }
        };
        handler.post(runnable);
    }

    public void setSwappedFeeds(boolean isSwappedFeeds) {
        Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
//        this.isSwappedFeeds = isSwappedFeeds;
        localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);
//        pipRenderer.setVisibility(View.GONE);
    }

    public void setRemoteDescription(String sdp) {
        SessionDescription sdpAnswer = new SessionDescription(
                SessionDescription.Type.fromCanonicalForm("answer"), sdp);
        onRemoteDescription(sdpAnswer);
    }

    //Signaling Event
    @Override
    public void onRemoteDescription(SessionDescription sdp) {
        if (peerConnectionClient == null) {
            Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
            return;
        }
        peerConnectionClient.setRemoteDescription(sdp);
        if (!signalingParameters.initiator) {
            Log.d(APP_NAME, "Creating ANSWER...");
            // Create answer. Answer SDP will be sent to offering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createAnswer();
        }
    }

    @Override
    public void onRemoteIceCandidate(IceCandidate candidate) {}

    @Override
    public void onRemoteIceCandidatesRemoved(IceCandidate[] candidates) {}

    @Override
    public void onChannelClose() {}

    @Override
    public void onChannelError(String description) {}


    //PeerConnection Event
    @Override
    public void onLocalDescription(SessionDescription sdp) {
//        Log.d("onLocalDescription", sdp.description);
        if (signalingParameters != null) {
            if (signalingParameters.initiator) {
                signalingParameters.offerSdp = sdp;
            } else {
                answerSdp = sdp;
            }
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {
//        peerConnectionClient.addRemoteIceCandidate(candidate);
        Ice ice = new Ice();
        if (signalingParameters!=null) {
            if (signalingParameters.initiator && signalingParameters.offerSdp!=null) {
                signalingParameters.offerSdp = new SessionDescription(signalingParameters.offerSdp.type, ice.addCandidate(signalingParameters.offerSdp.description, candidate.sdp));
//            Log.d("onIceCandidate", signalingParameters.offerSdp.description);
            } else if (!signalingParameters.initiator && answerSdp!=null) {
                answerSdp = new SessionDescription(answerSdp.type, ice.addCandidate(answerSdp.description, candidate.sdp));
            }
        } else {
            Log.d("onIceCandidate", candidate.sdp);
        }
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {}

    @Override
    public void onIceConnected() {
        //vantaCall.onIceConnected();
    }

    @Override
    public void onIceGatheringComplete() {
        if (signalingParameters.offerSdp!=null) {
            Log.d("onIceGatheringComplete", signalingParameters.offerSdp.description);
        } else {
            Log.d("onIceGatheringComplete", "a;sldkfjasdlfkj");
        }
    }

    @Override
    public void onIceDisconnected() {}

    @Override
    public void onConnected() {}

    @Override
    public void onDisconnected() {}

    @Override
    public void onPeerConnectionClosed() {}

    @Override
    public void onPeerConnectionStatsReady(StatsReport[] reports) {}

    @Override
    public void onPeerConnectionError(String description) {}
}
