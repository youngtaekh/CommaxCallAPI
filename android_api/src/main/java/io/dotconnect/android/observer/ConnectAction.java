package io.dotconnect.android.observer;

import java.util.ArrayList;
import java.util.List;

public class ConnectAction implements ConnectPublisher {
    private List<ConnectObserver.RegistrationObserver> registrationObservers;
    private List<ConnectObserver.MessageObserver> messageObservers;
    private List<ConnectObserver.CallObserver> callObservers;

    private static ConnectAction instance;

    public static ConnectAction getInstance() {
        if (instance == null)
            instance = new ConnectAction();
        return instance;
    }
    private ConnectAction() {
        registrationObservers = new ArrayList<>();
        messageObservers = new ArrayList<>();
        callObservers = new ArrayList<>();
    }

    @Override
    public void add(ConnectObserver.RegistrationObserver observer) {
        if (!registrationObservers.contains(observer))
            registrationObservers.add(observer);
    }

    @Override
    public void delete(ConnectObserver.RegistrationObserver observer) {
        registrationObservers.remove(observer);
    }

    @Override
    public void add(ConnectObserver.MessageObserver observer) {
        if (!messageObservers.contains(observer))
            messageObservers.add(observer);
    }

    @Override
    public void delete(ConnectObserver.MessageObserver observer) {
        messageObservers.add(observer);
    }

    @Override
    public void add(ConnectObserver.CallObserver observer) {
        if (!callObservers.contains(observer))
            callObservers.add(observer);
    }

    @Override
    public void delete(ConnectObserver.CallObserver observer) {
        callObservers.remove(observer);
    }

    @Override
    public void onDeviceRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onDeviceRegistrationSuccess();
        }
    }

    @Override
    public void onDeviceUnRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onDeviceUnRegistrationSuccess();
        }
    }

    @Override
    public void onRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onRegistrationSuccess();
        }
    }

    @Override
    public void onRegistrationFailureObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onRegistrationFailure();
        }
    }

    @Override
    public void onUnRegistrationSuccessObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onUnRegistrationSuccess();
        }
    }

    @Override
    public void onSocketClosureObserver() {
        for (ConnectObserver.RegistrationObserver registrationObserver : registrationObservers) {
            registrationObserver.onSocketClosure();
        }
    }

    @Override
    public void onMessageSendSuccessObserver(MessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendSuccess(message);
        }
    }

    @Override
    public void onMessageSendFailureObserver(MessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendFailure(message);
        }
    }

    @Override
    public void onMessageArrivalObserver(MessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageArrival(message);
        }
    }

    @Override
    public void onIncomingCallObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCall(callInfo);
        }
    }

    @Override
    public void onOutgoingCallObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onOutgoingCall(callInfo);
        }
    }

    @Override
    public void onUpdateObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onUpdate(callInfo);
        }
    }

    @Override
    public void onEarlyMediaObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onEarlyMedia(callInfo);
        }
    }

    @Override
    public void onOutgoingCallConnectedObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onOutgoingCallConnected(callInfo);
        }
    }

    @Override
    public void onIncomingCallConnectedObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCallConnected(callInfo);
        }
    }

    @Override
    public void onFailureObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onFailure(callInfo);
        }
    }

    @Override
    public void onTerminatedObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onTerminated(callInfo);
        }
    }

    @Override
    public void onBusyOnIncomingCallObserver(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onBusyOnIncomingCall(callInfo);
        }
    }

    @Override
    public void onCancelCallBefore180Observer(CallInfo callInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onCancelCallBefore180(callInfo);
        }
    }
}
