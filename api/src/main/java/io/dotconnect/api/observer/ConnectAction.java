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
    public void onMessageSendSuccessObserver(APIMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendSuccess(message);
        }
    }

    @Override
    public void onMessageSendFailureObserver(APIMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageSendFailure(message);
        }
    }

    @Override
    public void onMessageArrivalObserver(APIMessageInfo message) {
        for (ConnectObserver.MessageObserver messageObserver : messageObservers) {
            messageObserver.onMessageArrival(message);
        }
    }

    @Override
    public void onIncomingCallObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCall(APICallInfo);
        }
    }

    @Override
    public void onOutgoingCallObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onOutgoingCall(APICallInfo);
        }
    }

    @Override
    public void onUpdateObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onUpdate(APICallInfo);
        }
    }

    @Override
    public void onEarlyMediaObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onEarlyMedia(APICallInfo);
        }
    }

    @Override
    public void onOutgoingCallConnectedObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onOutgoingCallConnected(APICallInfo);
        }
    }

    @Override
    public void onIncomingCallConnectedObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onIncomingCallConnected(APICallInfo);
        }
    }

    @Override
    public void onFailureObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onFailure(APICallInfo);
        }
    }

    @Override
    public void onTerminatedObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
            callObserver.onTerminated(APICallInfo);
        }
    }

    @Override
    public void onBusyOnIncomingCallObserver(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onBusyOnIncomingCall(APICallInfo);
        }
    }

    @Override
    public void onCancelCallBefore180Observer(APICallInfo APICallInfo) {
        for (ConnectObserver.CallObserver callObserver : callObservers) {
//            callObserver.onCancelCallBefore180(APICallInfo);
        }
    }
}
