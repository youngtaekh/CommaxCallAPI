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
        void onOutgoingCall(SignalingCallInfo signalingCallInfo);
        void onUpdate(SignalingCallInfo signalingCallInfo);
        void onEarlyMedia(SignalingCallInfo signalingCallInfo);
        void onOutgoingCallConnected(SignalingCallInfo signalingCallInfo);
        void onIncomingCallConnected(SignalingCallInfo signalingCallInfo);
        void onFailure(SignalingCallInfo signalingCallInfo);
        void onTerminated(SignalingCallInfo signalingCallInfo);
        void onBusyOnIncomingCall(SignalingCallInfo signalingCallInfo);
        void onCancelCallBefore180(SignalingCallInfo signalingCallInfo);

//        void onProvisional(SignalingCallInfo callInfo);
//        void onPrack(SignalingCallInfo callInfo);
//        void onStableCallTimeout(SignalingCallInfo callInfo);
//        void onRedirected(SignalingCallInfo callInfo);
//        void onAnswer(SignalingCallInfo callInfo);
//        void onOffer(SignalingCallInfo callInfo);
//        void onOfferRequired(SignalingCallInfo callInfo);
//        void onOfferRejected(SignalingCallInfo callInfo);
//        void onOfferRequestRejected(SignalingCallInfo callInfo);
//        void onRemoteSdpChanged(SignalingCallInfo callInfo);
//        void onInfo(SignalingCallInfo callInfo);
//        void onInfoSuccess(SignalingCallInfo callInfo);
//        void onInfoFailure(SignalingCallInfo callInfo);
//        void onRefer(SignalingCallInfo callInfo);
//        void onReferAccepted(SignalingCallInfo callInfo);
//        void onReferRejected(SignalingCallInfo callInfo);
//        void onReferNoSub(SignalingCallInfo callInfo);
//        void onMessage(SignalingCallInfo callInfo);
//        void onMessageSuccess(SignalingCallInfo callInfo);
//        void onMessageFailure(SignalingCallInfo callInfo);
//        void onForkDestroyed(SignalingCallInfo callInfo);
//        void onReadyToSend(SignalingCallInfo callInfo);
//        void onFlowTerminated(SignalingCallInfo callInfo);
    }
}
