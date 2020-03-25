package io.dotconnect.p2p;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import io.dotconnect.p2p.wwc.Camera;
import io.dotconnect.p2p.wwc.Ice;

import static io.dotconnect.api.util.APIConfiguration.APP_NAME;
import static io.dotconnect.p2p.utils.Configuration.*;

import org.webrtc.*;

import java.util.ArrayList;
import java.util.List;

public class P2PManager {

    private AppRTCClient.SignalingParameters signalingParameters = null;
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters = null;
    private PeerConnectionClient peerConnectionClient = null;

    private SurfaceViewRenderer pipRenderer = null;
    private SurfaceViewRenderer fullscreenRenderer = null;

    private ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private EglBase eglBase;
    private ArrayList<VideoSink> remoteSinks = new ArrayList<>();

    private Handler handler = null;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendSdp();
        }
    };

    private SDPListener listener;

    public class ProxyVideoSink implements VideoSink {
        private VideoSink target = null;

        @Override
        synchronized public void onFrame(VideoFrame videoFrame) {
            if (target == null) {
                Logging.d("asdf", "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(videoFrame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }

    private PeerConnectionClient.PeerConnectionEvents events = new PeerConnectionClient.PeerConnectionEvents() {
        @Override
        public void onLocalDescription(SessionDescription sdp) {
            if (signalingParameters != null) {
                if (signalingParameters.initiator) {
                    signalingParameters.offerSdp = sdp;
                } else {
                    signalingParameters.answerSdp = sdp;
                }
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(runnable, 1000);
                }
            }
        }

        @Override
        public void onIceCandidate(IceCandidate candidate) {
            Ice ice = new Ice();
            if (signalingParameters != null) {
                if (signalingParameters.initiator && signalingParameters.offerSdp != null) {
                    signalingParameters.offerSdp = new SessionDescription(signalingParameters.offerSdp.type,
                            ice.addCandidate(signalingParameters.offerSdp.description, candidate.sdp));
                    //            Log.d("onIceCandidate", signalingParameters.offerSdp.description);
                } else if (!signalingParameters.initiator && signalingParameters.answerSdp != null) {
                    signalingParameters.answerSdp = new SessionDescription(signalingParameters.answerSdp.type,
                            ice.addCandidate(signalingParameters.answerSdp.description, candidate.sdp));
                }
            } else {
                Log.d("onIceCandidate", candidate.sdp);
            }
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] candidates) {

        }

        @Override
        public void onIceConnected() {

        }

        @Override
        public void onIceGatheringComplete() {

        }

        @Override
        public void onIceDisconnected() {

        }

        @Override
        public void onConnected() {
            setSwappedFeeds(false);
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onPeerConnectionClosed() {

        }

        @Override
        public void onPeerConnectionStatsReady(StatsReport[] reports) {

        }

        @Override
        public void onPeerConnectionError(String description) {

        }
    };

    public P2PManager() {
        eglBase = EglBase.create();
    }

    public void setRenderer(SurfaceViewRenderer fullscreenRenderer, SurfaceViewRenderer pipRenderer) {
        this.fullscreenRenderer = fullscreenRenderer;
        this.pipRenderer = pipRenderer;
    }

    public void initRenderer() {
        Log.d(APP_NAME, "initRenderer()");
        remoteSinks.add(remoteProxyRenderer);

        // Create video renderers.
        if (pipRenderer!=null) {
            pipRenderer.init(eglBase.getEglBaseContext(), null);
            pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            pipRenderer.setZOrderMediaOverlay(true);
            pipRenderer.setEnableHardwareScaler(true /* enabled */);
        }

        if (fullscreenRenderer!=null) {
            fullscreenRenderer.init(eglBase.getEglBaseContext(), null);
            fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            fullscreenRenderer.setEnableHardwareScaler(false /* enabled */);
        }
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        setSwappedFeeds(false /* isSwappedFeeds */);
    }

    public void setParameters(Context context, boolean audio, boolean isVideoCall, boolean videoRecvOnly) {
        boolean loopback = false;

        peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(
                audio, isVideoCall, videoRecvOnly, false,
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
                ENABLE_RTC_EVENT_LOG, null, null);

        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient(
                context.getApplicationContext(), eglBase, peerConnectionParameters, events);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        if (loopback) {
            options.networkIgnoreMask = 0;
        }
        peerConnectionClient.createPeerConnectionFactory(options);
    }

    public void startCall(Context context, boolean isOffer, String description, SDPListener listener) {
        this.listener = listener;
        // Request TURN servers.
        List<PeerConnection.IceServer> turnServers = new Ice().getIceServers();
        List<PeerConnection.IceServer> iceServers = new ArrayList<>(turnServers);

        SessionDescription offerSDP = null;
        if (!isOffer) {
            offerSDP = new SessionDescription(SessionDescription.Type.OFFER, description);
        }

        signalingParameters =
                new AppRTCClient.SignalingParameters(iceServers, isOffer, offerSDP, null);
        VideoCapturer videoCapturer = null;
        if (!peerConnectionParameters.videoRecvOnly
                && peerConnectionParameters.videoCallEnabled) {
            videoCapturer = new Camera(context).createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(
                localProxyVideoSink, remoteSinks, videoCapturer, signalingParameters
        );

        if (signalingParameters.initiator) {
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (signalingParameters.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(signalingParameters.offerSdp);
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (signalingParameters.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : signalingParameters.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    public void setSwappedFeeds(boolean isSwappedFeeds) {
        if (pipRenderer!=null && fullscreenRenderer!=null) {
            localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
            remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
            fullscreenRenderer.setMirror(isSwappedFeeds);
            pipRenderer.setMirror(!isSwappedFeeds);
            pipRenderer.setZOrderMediaOverlay(true);
        } else {
            if (fullscreenRenderer!=null) {
                remoteProxyRenderer.setTarget(fullscreenRenderer);
                fullscreenRenderer.setMirror(isSwappedFeeds);
            } else if (pipRenderer!=null) {
                remoteProxyRenderer.setTarget(pipRenderer);
                pipRenderer.setMirror(!isSwappedFeeds);
                pipRenderer.setZOrderMediaOverlay(true);
            }
        }
    }

    public void setScaleType(RendererCommon.ScalingType scaleType) {
        fullscreenRenderer.setScalingType(scaleType);
    }

    public void setRemoteDescription(String description) {
        SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, description);
        peerConnectionClient.setRemoteDescription(sessionDescription);
    }

    private void sendSdp() {
        if (signalingParameters != null) {
            if (signalingParameters.initiator && signalingParameters.offerSdp != null) {
                listener.onLocalDescription(signalingParameters.offerSdp.description);
//                CallCore.getInstance().makeVideoCall(target, teamId, signalingParameters.offerSdp.description);
            } else if(signalingParameters.answerSdp != null) {
                listener.onLocalDescription(signalingParameters.answerSdp.description);
//                CallCore.getInstance().acceptCall(signalingParameters.answerSdp.description);
            }
        }
    }

    public void disconnect() {
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
        handler=null;
    }
}
