package io.dotconnect.signaling.observer;

public class SignalingObserver {
    public interface RegistrationObserver {
        void onRegistrationSuccess();
        void onRegistrationFailure();
        void onUnRegistrationSuccess();
        void onSocketClosure();
    }

    public interface MessageObserver {
        void onMessageSendSuccess(Message message);
        void onMessageSendFailure(Message message);
        void onMessageArrival(Message message);
    }

    public interface CallObserver {
        void onIncomingCall(Call call);
        void onOutgoingCall(Call call);
        void onUpdate(Call call);
        void onEarlyMedia(Call call);
        void onOutgoingCallConnected(Call call);
        void onIncomingCallConnected(Call call);
        void onFailure(Call call);
        void onTerminated(Call call);
        void onBusyOnIncomingCall(Call call);
        void onCancelCallBefore180(Call call);

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
