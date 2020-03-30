package io.dotconnect.signaling.observer;

import java.util.ArrayList;
import java.util.List;

public class SignalingAction implements SignalingPublisher {
    private List<SignalingObserver.RegistrationObserver> registrationObservers;
    private List<SignalingObserver.MessageObserver> messageObservers;
    private List<SignalingObserver.CallObserver> callObservers;

    private static SignalingAction instance;

    private SignalingAction() {
        registrationObservers = new ArrayList<>();
        messageObservers = new ArrayList<>();
        callObservers = new ArrayList<>();
    }

    public static SignalingAction getInstance() {
        if (instance == null)
            instance = new SignalingAction();
        return instance;
    }

    @Override
    public void add(SignalingObserver.RegistrationObserver observer) {
        if (!registrationObservers.contains(observer))
            registrationObservers.add(observer);
    }

    @Override
    public void delete(SignalingObserver.RegistrationObserver observer) {
        registrationObservers.remove(observer);
    }

    @Override
    public void add(SignalingObserver.MessageObserver observer) {
        if (!messageObservers.contains(observer))
            messageObservers.add(observer);
    }

    @Override
    public void delete(SignalingObserver.MessageObserver observer) {
        messageObservers.remove(observer);
    }

    @Override
    public void add(SignalingObserver.CallObserver observer) {
        if (!callObservers.contains(observer))
            callObservers.add(observer);
    }

    @Override
    public void delete(SignalingObserver.CallObserver observer) {
        callObservers.remove(observer);
    }

    @Override
    public void onRegistrationSuccessObserver() {
        for (SignalingObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onRegistrationSuccess();
        }
    }

    @Override
    public void onRegistrationFailureObserver() {
        for (SignalingObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onRegistrationFailure();
        }
    }

    @Override
    public void onUnRegistrationSuccessObserver() {
        for (SignalingObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onUnRegistrationSuccess();
        }
    }

    @Override
    public void onSocketClosureObserver() {
        for (SignalingObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onSocketClosure();
        }
    }

    @Override
    public void onMessageSendSuccessObserver(SignalingMessageInfo signalingMessageInfo) {
        for (SignalingObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendSuccess(signalingMessageInfo);
        }
    }

    @Override
    public void onMessageSendFailureObserver(SignalingMessageInfo signalingMessageInfo) {
        for (SignalingObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendFailure(signalingMessageInfo);
        }
    }

    @Override
    public void onMessageArrivalObserver(SignalingMessageInfo signalingMessageInfo) {
        for (SignalingObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageArrival(signalingMessageInfo);
        }
    }

    @Override
    public void onIncomingCallObserver(SignalingCallInfo signalingCallInfo) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCall(signalingCallInfo);
        }
    }

    @Override
    public void onCallConnectedObserver(SignalingCallInfo signalingCallInfo) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onCallConnected(signalingCallInfo);
        }
    }

    @Override
    public void onFailureObserver(SignalingCallInfo signalingCallInfo) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onFailure(signalingCallInfo);
        }
    }

    @Override
    public void onTerminatedObserver(SignalingCallInfo signalingCallInfo) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onTerminated(signalingCallInfo);
        }
    }

    @Override
    public void onOfferObserver(SignalingCallInfo signalingCallInfo) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onOffer(signalingCallInfo);
        }
    }

    @Override
    public void onBusyOnIncomingCallObserver(SignalingCallInfo signalingCallInfo) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onBusyOnIncomingCall(signalingCallInfo);
        }
    }

    @Override
    public void onCancelCallBefore180Observer(SignalingCallInfo signalingCallInfo) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onCancelCallBefore180(signalingCallInfo);
        }
    }
}
