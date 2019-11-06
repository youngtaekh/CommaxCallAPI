package io.dotconnect.api.observer;

public interface ConnectPublisher {
    void add(ConnectObserver.RegistrationObserver observer);
    void delete(ConnectObserver.RegistrationObserver observer);

    void add(ConnectObserver.MessageObserver observer);
    void delete(ConnectObserver.MessageObserver observer);

    void add(ConnectObserver.CallObserver observer);
    void delete(ConnectObserver.CallObserver observer);

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
    void onOutgoingCallObserver(ApiCallInfo apiCallInfo);
    void onUpdateObserver(ApiCallInfo apiCallInfo);
    void onEarlyMediaObserver(ApiCallInfo apiCallInfo);
    void onOutgoingCallConnectedObserver(ApiCallInfo apiCallInfo);
    void onIncomingCallConnectedObserver(ApiCallInfo apiCallInfo);
    void onFailureObserver(ApiCallInfo apiCallInfo);
    void onTerminatedObserver(ApiCallInfo apiCallInfo);
    void onBusyOnIncomingCallObserver(ApiCallInfo apiCallInfo);
    void onCancelCallBefore180Observer(ApiCallInfo apiCallInfo);
}
