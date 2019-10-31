package io.dotconnect.android.observer;

public interface ConnectPublisher {
    void add(ConnectObserver.RegistrationObserver observer);
    void delete(ConnectObserver.RegistrationObserver observer);

    void add(ConnectObserver.MessageObserver observer);
    void delete(ConnectObserver.MessageObserver observer);

    void add(ConnectObserver.CallObserver observer);
    void delete(ConnectObserver.CallObserver observer);

    void onRegistrationSuccessObserver();
    void onRegistrationFailureObserver();
    void onUnRegistrationSuccessObserver();
    void onSocketClosureObserver();

    void onMessageSendSuccessObserver(MessageInfo message);
    void onMessageSendFailureObserver(MessageInfo message);
    void onMessageArrivalObserver(MessageInfo message);

    void onIncomingCallObserver(CallInfo callInfo);
    void onOutgoingCallObserver(CallInfo callInfo);
    void onUpdateObserver(CallInfo callInfo);
    void onEarlyMediaObserver(CallInfo callInfo);
    void onOutgoingCallConnectedObserver(CallInfo callInfo);
    void onIncomingCallConnectedObserver(CallInfo callInfo);
    void onFailureObserver(CallInfo callInfo);
    void onTerminatedObserver(CallInfo callInfo);
    void onBusyOnIncomingCallObserver(CallInfo callInfo);
    void onCancelCallBefore180Observer(CallInfo callInfo);
}
