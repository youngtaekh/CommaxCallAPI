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
        void onMessageSendSuccess(MessageInfo message);
        void onMessageSendFailure(MessageInfo message);
        void onMessageArrival(MessageInfo message);
    }

    public interface CallObserver {
        void onIncomingCall(CallInfo callInfo);
//        void onOutgoingCall(CallInfo callInfo);
//        void onUpdate(CallInfo callInfo);
//        void onEarlyMedia(CallInfo callInfo);
//        void onOutgoingCallConnected(CallInfo callInfo);
        void onIncomingCallConnected(CallInfo callInfo);
        void onFailure(CallInfo callInfo);
        void onTerminated(CallInfo callInfo);
//        void onBusyOnIncomingCall(CallInfo callInfo);
//        void onCancelCallBefore180(CallInfo callInfo);

//        void onProvisional(Call callInfo);
//        void onPrack(Call callInfo);
//        void onStableCallTimeout(Call callInfo);
//        void onRedirected(Call callInfo);
//        void onAnswer(Call callInfo);
//        void onOffer(Call callInfo);
//        void onOfferRequired(Call callInfo);
//        void onOfferRejected(Call callInfo);
//        void onOfferRequestRejected(Call callInfo);
//        void onRemoteSdpChanged(Call callInfo);
//        void onInfo(Call callInfo);
//        void onInfoSuccess(Call callInfo);
//        void onInfoFailure(Call callInfo);
//        void onRefer(Call callInfo);
//        void onReferAccepted(Call callInfo);
//        void onReferRejected(Call callInfo);
//        void onReferNoSub(Call callInfo);
//        void onMessage(Call callInfo);
//        void onMessageSuccess(Call callInfo);
//        void onMessageFailure(Call callInfo);
//        void onForkDestroyed(Call callInfo);
//        void onReadyToSend(Call callInfo);
//        void onFlowTerminated(Call callInfo);
    }
}
