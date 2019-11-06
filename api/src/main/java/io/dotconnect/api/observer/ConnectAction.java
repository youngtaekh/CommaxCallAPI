package io.dotconnect.api.observer;

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
    public void onMessageSendSuccessObserver(ApiMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendSuccess(message);
        }
    }

    @Override
    public void onMessageSendFailureObserver(ApiMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendFailure(message);
        }
    }

    @Override
    public void onMessageArrivalObserver(ApiMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageArrival(message);
        }
    }

    @Override
    public void onIncomingCallObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCall(apiCallInfo);
        }
    }

    @Override
    public void onOutgoingCallObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onOutgoingCall(apiCallInfo);
        }
    }

    @Override
    public void onUpdateObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onUpdate(apiCallInfo);
        }
    }

    @Override
    public void onEarlyMediaObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onEarlyMedia(apiCallInfo);
        }
    }

    @Override
    public void onOutgoingCallConnectedObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onOutgoingCallConnected(apiCallInfo);
        }
    }

    @Override
    public void onIncomingCallConnectedObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCallConnected(apiCallInfo);
        }
    }

    @Override
    public void onFailureObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onFailure(apiCallInfo);
        }
    }

    @Override
    public void onTerminatedObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onTerminated(apiCallInfo);
        }
    }

    @Override
    public void onBusyOnIncomingCallObserver(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onBusyOnIncomingCall(apiCallInfo);
        }
    }

    @Override
    public void onCancelCallBefore180Observer(ApiCallInfo apiCallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onCancelCallBefore180(apiCallInfo);
        }
    }
}