package io.dotconnect.api.observer;

import java.util.List;

import io.dotconnect.signaling.observer.SignalingCallInfo;

public class ConnectObserver {
    public interface RegistrationObserver {
        void onDeviceRegistrationSuccess();
        void onDeviceUnRegistrationSuccess();
        void onRegistrationSuccess();
        void onRegistrationFailure();
        void onUnRegistrationSuccess();
        void onSocketClosure();
    }

    public interface MessageObserver {
        void onMessageSendSuccess(ApiMessageInfo message);
        void onMessageSendFailure(ApiMessageInfo message);
        void onMessageArrival(ApiMessageInfo message);
        void onCctvList(String json);
    }

    public interface CallObserver {
        void onIncomingCall(ApiCallInfo apiCallInfo);
        void onIncomingCallConnected(ApiCallInfo apiCallInfo);
        void onFailure(ApiCallInfo apiCallInfo);
        void onTerminated(ApiCallInfo apiCallInfo);
    }

    public interface PeerConnectionObserver {
        void onPeerConnectionConnected();
        void onPeerConnectionDisconnected();
        void onPeerConnectionFailed();
        void onPeerConnectionClosed();
        void onPeerConnectionError(String description);
    }
}
