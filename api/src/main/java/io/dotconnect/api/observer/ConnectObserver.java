package io.dotconnect.api.observer;

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
    }

    public interface CallObserver {
        void onIncomingCall(ApiCallInfo apiCallInfo);
//        void onOutgoingCall(ApiCallInfo ApiCallInfo);
//        void onUpdate(ApiCallInfo ApiCallInfo);
//        void onEarlyMedia(ApiCallInfo ApiCallInfo);
//        void onOutgoingCallConnected(ApiCallInfo ApiCallInfo);
        void onIncomingCallConnected(ApiCallInfo apiCallInfo);
        void onFailure(ApiCallInfo apiCallInfo);
        void onTerminated(ApiCallInfo apiCallInfo);
//        void onBusyOnIncomingCall(ApiCallInfo callInfo);
//        void onCancelCallBefore180(ApiCallInfo callInfo);

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
