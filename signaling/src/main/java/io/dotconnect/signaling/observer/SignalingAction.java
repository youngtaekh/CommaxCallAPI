package io.dotconnect.signaling.observer;

import java.util.ArrayList;
import java.util.List;

public class SignalingAction implements SignalingPublisher {
    private List<SignalingObserver.RegistrationObserver> registrationObservers;
    private List<SignalingObserver.MessageObserver> messageObservers;
    private List<SignalingObserver.CallObserver> callObservers;

    private static SignalingAction instance;

    public SignalingAction() {
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
    public void onMessageSendSuccessObserver(Message message) {
        for (SignalingObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendSuccess(message);
        }
    }

    @Override
    public void onMessageSendFailureObserver(Message message) {
        for (SignalingObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendFailure(message);
        }
    }

    @Override
    public void onMessageArrivalObserver(Message message) {
        for (SignalingObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageArrival(message);
        }
    }

    @Override
    public void onIncomingCallObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCall(call);
        }
    }

    @Override
    public void onOutgoingCallObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onOutgoingCall(call);
        }
    }

    @Override
    public void onUpdateObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onUpdate(call);
        }
    }

    @Override
    public void onEarlyMediaObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onEarlyMedia(call);
        }
    }

    @Override
    public void onOutgoingCallConnectedObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onOutgoingCallConnected(call);
        }
    }

    @Override
    public void onIncomingCallConnectedObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCallConnected(call);
        }
    }

    @Override
    public void onFailureObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onFailure(call);
        }
    }

    @Override
    public void onTerminatedObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onTerminated(call);
        }
    }

    @Override
    public void onBusyOnIncomingCallObserver(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onBusyOnIncomingCall(call);
        }
    }

    @Override
    public void onCancelCallBefore180Observer(Call call) {
        for (SignalingObserver.CallObserver callObserver : callObservers) {
            callObserver.onCancelCallBefore180(call);
        }
    }
}
