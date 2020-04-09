package io.dotconnect.p2p;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import io.dotconnect.p2p.observer.P2PAction;
import io.dotconnect.p2p.utils.Ice;

import static io.dotconnect.api.util.APIConfiguration.APP_NAME;
import static io.dotconnect.p2p.utils.Configuration.*;

import org.webrtc.*;

import java.util.ArrayList;
import java.util.List;

public class P2PManager {

    private AppRTCClient.SignalingParameters signalingParameters = null;
    private PeerConnectionClient peerConnectionClient = null;

    private SurfaceViewRenderer fullscreenRenderer = null;

    private ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private EglBase eglBase;
    private ArrayList<VideoSink> remoteSinks = new ArrayList<>();

    private Handler handler = null;
    private Runnable runnable = this::sendSdp;

    private SDPListener listener;

    public static class ProxyVideoSink implements VideoSink {
        private VideoSink target = null;

        @Override
        synchronized public void onFrame(VideoFrame videoFrame) {
            if (target == null) {
                Logging.d(APP_NAME, "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(videoFrame);
        }

        synchronized void setTarget(VideoSink target) {
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
                } else if (!signalingParameters.initiator && signalingParameters.answerSdp != null) {
                    signalingParameters.answerSdp = new SessionDescription(signalingParameters.answerSdp.type,
                            ice.addCandidate(signalingParameters.answerSdp.description, candidate.sdp));
                }
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
            P2PAction.getInstance().onConnectedObserver();
        }

        @Override
        public void onDisconnected() {
            P2PAction.getInstance().onDisconnectedObserver();
        }

        @Override
        public void onFailed() {
            P2PAction.getInstance().onFailedObserver();
        }

        @Override
        public void onPeerConnectionClosed() {
            P2PAction.getInstance().onClosedObserver();
        }

        @Override
        public void onPeerConnectionStatsReady(StatsReport[] reports) {

        }

        @Override
        public void onPeerConnectionError(String description) {
            P2PAction.getInstance().onErrorObserver(description);
        }
    };

    public P2PManager() {
        eglBase = EglBase.create();
    }

    public void setRenderer(SurfaceViewRenderer fullscreenRenderer) {
        this.fullscreenRenderer = fullscreenRenderer;
    }

    public void initRenderer() {
        Log.d(APP_NAME, "P2PManager - initRenderer()");
        remoteSinks.add(remoteProxyRenderer);

        // Create video renderers.
        if (fullscreenRenderer!=null) {
            fullscreenRenderer.init(eglBase.getEglBaseContext(), null);
            fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            fullscreenRenderer.setEnableHardwareScaler(false /* enabled */);
            remoteProxyRenderer.setTarget(fullscreenRenderer);
            fullscreenRenderer.setMirror(false);
        }
    }

    public void setParameters(Context context, boolean audio, boolean isVideoCall) {
        PeerConnectionClient.PeerConnectionParameters peerConnectionParameters
                = new PeerConnectionClient.PeerConnectionParameters(
                audio, isVideoCall,
                TRACING,
                VIDEO_MAX_BITRATE, VIDEO_CODEC,
                VIDEO_CODEC_HW_ACCELERATION,
                VIDEO_FLEXFEC_ENABLED,
                AUDIO_START_BITRATE, AUDIO_CODEC,
                DISABLE_BUILT_IN_AEC,
                DISABLE_BUILT_IN_NS,
                DISABLE_WEBRTC_AGC_AND_HPF);

        // Create peer connection client.
        peerConnectionClient = new PeerConnectionClient(
                context.getApplicationContext(), eglBase, peerConnectionParameters, events);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionClient.createPeerConnectionFactory(options);
    }

    public void startCall(boolean isOffer, String description, SDPListener listener) {
        this.listener = listener;
        // Request TURN servers.
        List<PeerConnection.IceServer> turnServers = new Ice().getIceServers();
        List<PeerConnection.IceServer> iceServers = new ArrayList<>(turnServers);

        SessionDescription offerSDP = null;
        if (!isOffer)
            offerSDP = new SessionDescription(SessionDescription.Type.OFFER, description);

        signalingParameters =
                new AppRTCClient.SignalingParameters(iceServers, isOffer, offerSDP, null);
        peerConnectionClient.createPeerConnection(
                localProxyVideoSink, remoteSinks, signalingParameters
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
