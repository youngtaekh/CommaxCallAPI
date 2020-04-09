package io.dotconnect.api.observer;

import java.util.ArrayList;
import java.util.List;

public class ConnectAction implements ConnectPublisher {
    private List<ConnectObserver.RegistrationObserver> registrationObservers;
    private List<ConnectObserver.MessageObserver> messageObservers;
    private List<ConnectObserver.CallObserver> callObservers;
    private List<ConnectObserver.PeerConnectionObserver> peerConnectionObservers;

    private static ConnectAction instance;

    public static ConnectAction getInstance() {
        if (instance == null)
            instance = new ConnectAction();
        return instance;
    }
    private ConnectAction() {
        registrationObservers = new ArrayList<>();
        messageObservers = new ArrayList<>();
        callObservers = new ArrayList<>();
        peerConnectionObservers = new ArrayList<>();
    }

    @Override
    public void add(ConnectObserver.RegistrationObserver observer) {
        if (!registrationObservers.contains(observer))
            registrationObservers.add(observer);
    }

    @Override
    public void delete(ConnectObserver.RegistrationObserver observer) {
        registrationObservers.remove(observer);
    }

    @Override
    public void add(ConnectObserver.MessageObserver observer) {
        if (!messageObservers.contains(observer))
            messageObservers.add(observer);
    }

    @Override
    public void delete(ConnectObserver.MessageObserver observer) {
        messageObservers.remove(observer);
    }

    @Override
    public void add(ConnectObserver.CallObserver observer) {
        if (!callObservers.contains(observer))
            callObservers.add(observer);
    }

    @Override
    public void delete(ConnectObserver.CallObserver observer) {
        callObservers.remove(observer);
    }

    @Override
    public void add(ConnectObserver.PeerConnectionObserver observer) {
        if (!peerConnectionObservers.contains(observer))
            peerConnectionObservers.add(observer);
    }

    @Override
    public void delete(ConnectObserver.PeerConnectionObserver observer) {
        peerConnectionObservers.remove(observer);
    }

    @Override
    public void onDeviceRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onDeviceRegistrationSuccess();
        }
    }

    @Override
    public void onDeviceUnRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onDeviceUnRegistrationSuccess();
        }
    }

    @Override
    public void onRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onRegistrationSuccess();
        }
    }

    @Override
    public void onRegistrationFailureObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onRegistrationFailure();
        }
    }

    @Override
    public void onUnRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onUnRegistrationSuccess();
        }
    }

    @Override
    public void onSocketClosureObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onSocketClosure();
        }
    }

    @Override
    public void onMessageSendSuccessObserver(ApiMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendSuccess(message);
        }
    }

    @Override
    public void onMessageSendFailureObserver(ApiMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendFailure(message);
        }
    }

    @Override
    public void onMessageArrivalObserver(ApiMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageArrival(message);
        }
    }

    @Override
    public void onIncomingCallObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCall(apiCallInfo);
        }
    }

    @Override
    public void onCallConnectedObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCallConnected(apiCallInfo);
        }
    }

    @Override
    public void onFailureObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onFailure(apiCallInfo);
        }
    }

    @Override
    public void onTerminatedObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onTerminated(apiCallInfo);
        }
    }

    @Override
    public void onConnectedObserver() {
        for (ConnectObserver.PeerConnectionObserver peerConnectionObserver : peerConnectionObservers) {
            peerConnectionObserver.onPeerConnectionConnected();
        }
    }

    @Override
    public void onDisconnectedObserver() {
        for (ConnectObserver.PeerConnectionObserver peerConnectionObserver : peerConnectionObservers) {
            peerConnectionObserver.onPeerConnectionDisconnected();
        }
    }

    @Override
    public void onFailedObserver() {
        for (ConnectObserver.PeerConnectionObserver peerConnectionObserver : peerConnectionObservers) {
            peerConnectionObserver.onPeerConnectionFailed();
        }
    }

    @Override
    public void onClosedObserver() {
        for (ConnectObserver.PeerConnectionObserver peerConnectionObserver : peerConnectionObservers) {
            peerConnectionObserver.onPeerConnectionClosed();
        }
    }

    @Override
    public void onErrorObserver(String description) {
        for (ConnectObserver.PeerConnectionObserver peerConnectionObserver : peerConnectionObservers) {
            peerConnectionObserver.onPeerConnectionError(description);
        }
    }
}
