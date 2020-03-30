package io.dotconnect.signaling.observer;

public class SignalingObserver {
    public interface RegistrationObserver {
        void onRegistrationSuccess();
        void onRegistrationFailure();
        void onUnRegistrationSuccess();
        void onSocketClosure();
    }

    public interface MessageObserver {
        void onMessageSendSuccess(SignalingMessageInfo signalingMessageInfo);
        void onMessageSendFailure(SignalingMessageInfo signalingMessageInfo);
        void onMessageArrival(SignalingMessageInfo signalingMessageInfo);
    }

    public interface CallObserver {
        void onIncomingCall(SignalingCallInfo signalingCallInfo);
        void onCallConnected(SignalingCallInfo signalingCallInfo);
        void onFailure(SignalingCallInfo signalingCallInfo);
        void onTerminated(SignalingCallInfo signalingCallInfo);
        void onOffer(SignalingCallInfo signalingCallInfo);
        void onBusyOnIncomingCall(SignalingCallInfo signalingCallInfo);
        void onCancelCallBefore180(SignalingCallInfo signalingCallInfo);
    }
}
