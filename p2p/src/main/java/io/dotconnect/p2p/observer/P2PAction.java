package io.dotconnect.p2p.observer;

import java.util.ArrayList;
import java.util.List;

public class P2PAction implements P2PPublisher {
    private List<P2PObserver> p2pObservers;

    private static P2PAction instance;

    private P2PAction() {
        p2pObservers = new ArrayList<>();
    }

    public static P2PAction getInstance() {
        if (instance == null)
            instance = new P2PAction();
        return instance;
    }

    @Override
    public void add(P2PObserver observer) {
        if (!p2pObservers.contains(observer))
            p2pObservers.add(observer);
    }

    @Override
    public void delete(P2PObserver observer) {
        p2pObservers.remove(observer);
    }

    @Override
    public void onConnectedObserver() {
        for (P2PObserver p2PObserver : p2pObservers) {
            p2PObserver.onConnected();
        }
    }

    @Override
    public void onDisconnectedObserver() {
        for (P2PObserver p2PObserver : p2pObservers) {
            p2PObserver.onDisconnected();
        }
    }

    @Override
    public void onFailedObserver() {
        for (P2PObserver p2PObserver : p2pObservers) {
            p2PObserver.onFailed();
        }
    }

    @Override
    public void onClosedObserver() {
        for (P2PObserver p2PObserver : p2pObservers) {
            p2PObserver.onClosed();
        }
    }

    @Override
    public void onErrorObserver(String description) {
        for (P2PObserver p2PObserver : p2pObservers) {
            p2PObserver.onError(description);
        }
    }
}
