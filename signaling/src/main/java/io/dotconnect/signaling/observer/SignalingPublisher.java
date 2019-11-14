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

    void onMessageSendSuccessObserver(SignalingMessageInfo signalingMessageInfo);
    void onMessageSendFailureObserver(SignalingMessageInfo signalingMessageInfo);
    void onMessageArrivalObserver(SignalingMessageInfo signalingMessageInfo);

    void onIncomingCallObserver(SignalingCallInfo signalingCallInfo);
    void onOutgoingCallObserver(SignalingCallInfo signalingCallInfo);
    void onUpdateObserver(SignalingCallInfo signalingCallInfo);
    void onEarlyMediaObserver(SignalingCallInfo signalingCallInfo);
    void onOutgoingCallConnectedObserver(SignalingCallInfo signalingCallInfo);
    void onIncomingCallConnectedObserver(SignalingCallInfo signalingCallInfo);
    void onFailureObserver(SignalingCallInfo signalingCallInfo);
    void onTerminatedObserver(SignalingCallInfo signalingCallInfo);
    void onOfferObserver(SignalingCallInfo signalingCallInfo);
    void onBusyOnIncomingCallObserver(SignalingCallInfo signalingCallInfo);
    void onCancelCallBefore180Observer(SignalingCallInfo signalingCallInfo);
}
