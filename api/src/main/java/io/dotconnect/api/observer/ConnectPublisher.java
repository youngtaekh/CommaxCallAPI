package io.dotconnect.api.observer;

public interface ConnectPublisher {
    void add(ConnectObserver.RegistrationObserver observer);
    void delete(ConnectObserver.RegistrationObserver observer);

    void add(ConnectObserver.MessageObserver observer);
    void delete(ConnectObserver.MessageObserver observer);

    void add(ConnectObserver.CallObserver observer);
    void delete(ConnectObserver.CallObserver observer);

    void add(ConnectObserver.PeerConnectionObserver observer);
    void delete(ConnectObserver.PeerConnectionObserver observer);

    void onDeviceRegistrationSuccessObserver();
    void onDeviceUnRegistrationSuccessObserver();
    void onRegistrationSuccessObserver();
    void onRegistrationFailureObserver();
    void onUnRegistrationSuccessObserver();
    void onSocketClosureObserver();

    void onMessageSendSuccessObserver(ApiMessageInfo message);
    void onMessageSendFailureObserver(ApiMessageInfo message);
    void onMessageArrivalObserver(ApiMessageInfo message);

    void onIncomingCallObserver(ApiCallInfo apiCallInfo);
    void onCallConnectedObserver(ApiCallInfo apiCallInfo);
    void onFailureObserver(ApiCallInfo apiCallInfo);
    void onTerminatedObserver(ApiCallInfo apiCallInfo);

    void onConnectedObserver();
    void onDisconnectedObserver();
    void onFailedObserver();
    void onClosedObserver();
    void onErrorObserver(String description);
}
