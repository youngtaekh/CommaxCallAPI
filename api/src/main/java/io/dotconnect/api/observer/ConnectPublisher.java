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

    void onMessageSendSuccessObserver(APIMessageInfo message);
    void onMessageSendFailureObserver(APIMessageInfo message);
    void onMessageArrivalObserver(APIMessageInfo message);

    void onIncomingCallObserver(APICallInfo APICallInfo);
    void onOutgoingCallObserver(APICallInfo APICallInfo);
    void onUpdateObserver(APICallInfo APICallInfo);
    void onEarlyMediaObserver(APICallInfo APICallInfo);
    void onOutgoingCallConnectedObserver(APICallInfo APICallInfo);
    void onIncomingCallConnectedObserver(APICallInfo APICallInfo);
    void onFailureObserver(APICallInfo APICallInfo);
    void onTerminatedObserver(APICallInfo APICallInfo);
    void onBusyOnIncomingCallObserver(APICallInfo APICallInfo);
    void onCancelCallBefore180Observer(APICallInfo APICallInfo);
}
