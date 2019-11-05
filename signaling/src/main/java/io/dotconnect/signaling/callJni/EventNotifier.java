package io.dotconnect.signaling.callJni;

import android.util.Log;
import io.dotconnect.signaling.observer.Call;
import io.dotconnect.signaling.observer.SignalingAction;
import io.dotconnect.signaling.observer.Message;

import static io.dotconnect.android.util.Configuration.APP_NAME;

public class EventNotifier
{
    // Registration Events
    private static final int OnRegistrationSuccess      = 1001;
    private static final int OnRegistrationFailure      = 1002;
    private static final int OnUnRegistrationSuccess    = 1036;
    private static final int OnSocketClosure            = 1037;

    // InviteSessionHandler
    private static final int OnOutgoingCall             = 1003;
    private static final int OnIncomingCall             = 1004;
    private static final int OnUpdate                   = 1005;
    private static final int OnFailure                  = 1006;
    private static final int OnEarlyMedia               = 1007;
    private static final int OnProvisional              = 1008;
    private static final int OnPrack                    = 1009;
    private static final int OnOutgoingCallConnected    = 1010;
    private static final int OnIncomingCallConnected    = 1011;
    private static final int OnStableCallTimeout        = 1012;
    private static final int OnTerminated               = 1013;
    private static final int OnRedirected               = 1014;
    private static final int OnAnswer                   = 1015;
    private static final int OnOffer                    = 1016;
    private static final int OnOfferRequired            = 1017;
    private static final int OnOfferRejected            = 1018;
    private static final int OnOfferRequestRejected     = 1019;
    private static final int OnRemoteSdpChanged         = 1020;
    private static final int OnInfo                     = 1021;
    private static final int OnInfoSuccess              = 1022;
    private static final int OnInfoFailure              = 1023;
    private static final int OnRefer                    = 1024;
    private static final int OnReferAccepted            = 1025;
    private static final int OnReferRejected            = 1026;
    private static final int OnReferNoSub               = 1027;
    private static final int OnMessage                  = 1028;
    private static final int OnMessageSuccess           = 1029;
    private static final int OnMessageFailure           = 1030;
    private static final int OnForkDestroyed            = 1031;
    private static final int OnReadyToSend              = 1032;
    private static final int OnFlowTerminated           = 1033;
    private static final int OnBusyOnIncomingCall       = 1034;
    private static final int OnCancelCallBefore180      = 1035;

    // DialogSetHandler
    private static final int OnTrying                   = 2001;
    private static final int OnNonDialogCreatingProvisional = 2002;

    // MessageObserver
    private static final int OnMessageSendSuccess       = 3001;
    private static final int OnMessageSendFailure       = 3002;
    private static final int OnMessageArrival = 3003;

    // member variables
    private static EventNotifier instance;

    private SignalingAction signalingAction;

    // constructor (only can be instantiated via singleton)
    private EventNotifier() {
        signalingAction = SignalingAction.getInstance();
    }

    public static EventNotifier getInstance() {
        if  (instance == null){
            instance    = new EventNotifier();
        }

        return  instance;
    }

    public void eventDispatcher (String from, String jsonStr) {

        SipMessage sipMessage = new SipMessage(from, jsonStr);

        switch (sipMessage.getEventCode()) {
            case OnRegistrationSuccess          : onRegistrationSuccess();
                break;
            case OnRegistrationFailure          : onRegistrationFailure();
                break;
            case OnUnRegistrationSuccess        : onUnRegistrationSuccess();
                break;
            case OnSocketClosure                : onSocketClosure();
                break;
            case OnOutgoingCall                 : onOutgoingCall(sipMessage);
                break;
            case OnIncomingCall                 : onIncomingCall(sipMessage);
                break;
            case OnUpdate                       : onUpdate(sipMessage);
                break;
            case OnFailure                      : onFailure(sipMessage);
                break;
            case OnEarlyMedia                   : onEarlyMedia(sipMessage);
                break;
            case OnProvisional                  : onProvisional();
                break;
            case OnPrack                        : onPrack();
                break;
            case OnOutgoingCallConnected        : onOutgoingCallConnected(sipMessage);
                break;
            case OnIncomingCallConnected        : onIncomingCallConnected(sipMessage);
                break;
            case OnStableCallTimeout            : onStableCallTimeout();
                break;
            case OnTerminated                   : onTerminated(sipMessage);
                break;
            case OnRedirected                   : onRedirected(null);
                break;
            case OnAnswer                       : onAnswer(sipMessage);
                break;
            case OnOffer                        : onOffer(null);
                break;
            case OnOfferRequired                : onOfferRequired(null);
                break;
            case OnOfferRejected                : onOfferRejected(sipMessage);
                break;
            case OnOfferRequestRejected         : onOfferRequestRejected(null);
                break;
            case OnRemoteSdpChanged             : onRemoteSdpChanged(null);
                break;
            case OnInfo                         : onInfo(null);
                break;
            case OnInfoSuccess                  : onInfoSuccess(null);
                break;
            case OnInfoFailure                  : onInfoFailure(null);
                break;
            case OnRefer                        : onRefer(null);
                break;
            case OnReferAccepted                : onReferAccepted(null);
                break;
            case OnReferRejected                : onReferRejected(null);
                break;
            case OnReferNoSub                   : onReferNoSub(null);
                break;
            case OnMessage                      : onMessage(null);
                break;
            case OnMessageSuccess               : onMessageSuccess(null);
                break;
            case OnMessageFailure               : onMessageFailure(null);
                break;
            case OnForkDestroyed                : onForkDestroyed();
                break;
            case OnReadyToSend                  : onReadyToSend(null);
                break;
            case OnFlowTerminated               : onFlowTerminated();
                break;
            case OnBusyOnIncomingCall           : onBusyIncomingCall(sipMessage);
                break;
            case OnCancelCallBefore180          : onCancelCallBefore180(sipMessage);
                break;
            case OnTrying                       : onTrying(null);
                break;
            case OnNonDialogCreatingProvisional : onNonDialogCreatingProvisional(null);
                break;
            case OnMessageSendSuccess           : onMessageSendSuccess(sipMessage);
                break;
            case OnMessageSendFailure           : onMessageSendFailure(sipMessage);
                break;
            case OnMessageArrival               : onMessageArrival(sipMessage);
                break;
            default:
                break;
        }
    }

    /*
    class CheckToast implements Runnable {
        static final int REGISTRATION = 0;
        static final int UN_REGISTRATION = 1;
        int type;
        int duration;

        CheckToast(int type, int duration) {
            this.duration = duration;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                if (type == REGISTRATION)
                    Toast.makeText(mContext, "onRegistrationSuccess", Toast.LENGTH_SHORT).show();
                if (type == UN_REGISTRATION)
                    Toast.makeText(mContext, "onUnRegistrationSuccess", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
    */

    // Registration Events
    private void onRegistrationSuccess() {
        Log.d(APP_NAME, "onRegistrationSuccess");
        signalingAction.onRegistrationSuccessObserver();
    }

    private void onRegistrationFailure() {
        Log.d(APP_NAME, "onRegistrationFailure");
        signalingAction.onRegistrationFailureObserver();
    }

    private void onUnRegistrationSuccess() {
        Log.d(APP_NAME, "onUnRegistrationSuccess");
        signalingAction.onUnRegistrationSuccessObserver();
    }

    private void onSocketClosure() {
        Log.d(APP_NAME, "onSocketClosure");
        signalingAction.onSocketClosureObserver();
    }

    private void onOutgoingCall(SipMessage message) {
        Log.d(APP_NAME, "onOutgoingCall");
        signalingAction.onOutgoingCallObserver(new Call(message));
    }

    private void onIncomingCall(SipMessage message) {
        Log.d(APP_NAME, "onIncomingCall");
        signalingAction.onIncomingCallObserver(new Call(message));
    }

    private void onUpdate(SipMessage message) {
        Log.d(APP_NAME, "onUpdate");
        signalingAction.onUpdateObserver(new Call(message));
    }

    private void onFailure(SipMessage message) {
        Log.d(APP_NAME, "onFailure");
        signalingAction.onFailureObserver(new Call(message));
    }

    private void onEarlyMedia(SipMessage message) {
        Log.d(APP_NAME, "onEarlyMedia");
        signalingAction.onEarlyMediaObserver(new Call(message));
    }

    private void onProvisional() {
        Log.d(APP_NAME, "onProvisional");
    }

    private void onPrack() {
        Log.d(APP_NAME, "onPrack");
    }

    private void onOutgoingCallConnected(SipMessage message) {
        Log.d(APP_NAME, "onOutgoingCallConnected");
        signalingAction.onOutgoingCallConnectedObserver(new Call(message));
    }

    private void onIncomingCallConnected(SipMessage message) {
        Log.d(APP_NAME, "onIncomingCallConnected");
        signalingAction.onIncomingCallConnectedObserver(new Call(message));
    }

    private void onStableCallTimeout() {
        Log.d(APP_NAME, "onStableCallTimeout");
    }

    private void onTerminated(SipMessage message) {
        Log.d(APP_NAME, "onTerminated");
        signalingAction.onTerminatedObserver(new Call(message));
    }

    private void onRedirected(SipMessage message) {
        Log.d(APP_NAME, "onRedirected");
    }

    private void onAnswer(SipMessage message) {
        Log.d(APP_NAME, "onAnswer");
    }

    private void onOffer(SipMessage message) {
        Log.d(APP_NAME, "onOffer");
    }

    private void onOfferRequired(SipMessage message) {
        Log.d(APP_NAME, "onOfferRequired");
    }

    private void onOfferRejected(SipMessage message) {
        Log.d(APP_NAME, "onOfferRejected");
    }

    private void onOfferRequestRejected(SipMessage message) {
        Log.d(APP_NAME, "onOfferRequestRejected");
    }

    private void onRemoteSdpChanged(SipMessage message) {
        Log.d(APP_NAME, "onRemoteSdpChanged");
    }

    private void onInfo(SipMessage message) {
        Log.d(APP_NAME, "onInfo");
    }

    private void onInfoSuccess(SipMessage message) {
        Log.d(APP_NAME, "onInfoSuccess");
    }

    private void onInfoFailure(SipMessage message) {
        Log.d(APP_NAME, "onInfoFailure");
    }

    private void onRefer(SipMessage message) {
        Log.d(APP_NAME, "onRefer");
    }

    private void onReferAccepted(SipMessage message) {
        Log.d(APP_NAME, "onReferAccepted");
    }

    private void onReferRejected(SipMessage message) {
        Log.d(APP_NAME, "onReferRejected");
    }

    private void onReferNoSub(SipMessage message) {
        Log.d(APP_NAME, "onReferNoSub");
    }

    private void onMessage(SipMessage message) {
        Log.d(APP_NAME, "onMessage");
    }

    private void onMessageSuccess(SipMessage message) {
        Log.d(APP_NAME, "onMessageSuccess");
    }

    private void onMessageFailure(SipMessage message) {
        Log.d(APP_NAME, "onMessageFailure");
    }

    private void onForkDestroyed() {
        Log.d(APP_NAME, "onForkDestroyed");
    }

    private void onReadyToSend(SipMessage message) {
        Log.d(APP_NAME, "onReadyToSend");
    }

    private void onFlowTerminated() {
        Log.d(APP_NAME, "onFlowTerminated");
    }

    private void onBusyIncomingCall(SipMessage message) {
        Log.d(APP_NAME, "onBusyIncomingCall");
        signalingAction.onBusyOnIncomingCallObserver(new Call(message));
    }

    private void onCancelCallBefore180(SipMessage message) {
        Log.d(APP_NAME, "onCancelCallBefore180");
        signalingAction.onCancelCallBefore180Observer(new Call(message));
    }

    // DialogSetHandler
    private void onTrying(SipMessage message) {
        Log.d(APP_NAME, "onTrying");
    }

    private void onNonDialogCreatingProvisional(SipMessage message) {
        Log.d(APP_NAME, "onNonDialogCreatingProvisional");
    }

    //MessageObserver
    private void onMessageSendSuccess(SipMessage message) {
        Log.d(APP_NAME, "onMessageSendSuccess");
        signalingAction.onMessageSendSuccessObserver(new Message(message));
    }

    private void onMessageSendFailure(SipMessage message) {
        Log.d(APP_NAME, "onMessageSendFailure");
        signalingAction.onMessageSendFailureObserver(new Message(message));
    }

    private void onMessageArrival(SipMessage message) {
        Log.d(APP_NAME, "onMessageArrival");
        signalingAction.onMessageArrivalObserver(new Message(message));
    }
}