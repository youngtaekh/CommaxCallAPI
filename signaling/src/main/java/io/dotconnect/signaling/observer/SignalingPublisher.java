package io.dotconnect.signaling.observer;

public interface SignalingPublisher {
    void add(SignalingObserver.RegistrationObserver observer);
    void delete(SignalingObserver.RegistrationObserver observer);

    void add(SignalingObserver.MessageObserver observer);
    void delete(SignalingObserver.MessageObserver observer);

    void add(SignalingObserver.CallObserver observer);
    void delete(SignalingObserver.CallObserver observer);

    void onRegistrationSuccessObserver();
    void onRegistrationFailureObserver();
    void onUnRegistrationSuccessObserver();
    void onSocketClosureObserver();

    void onMessageSendSuccessObserver(Message message);
    void onMessageSendFailureObserver(Message message);
    void onMessageArrivalObserver(Message message);

    void onIncomingCallObserver(Call call);
    void onOutgoingCallObserver(Call call);
    void onUpdateObserver(Call call);
    void onEarlyMediaObserver(Call call);
    void onOutgoingCallConnectedObserver(Call call);
    void onIncomingCallConnectedObserver(Call call);
    void onFailureObserver(Call call);
    void onTerminatedObserver(Call call);
    void onBusyOnIncomingCallObserver(Call call);
    void onCancelCallBefore180Observer(Call call);
}
