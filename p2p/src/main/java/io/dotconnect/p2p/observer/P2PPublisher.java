package io.dotconnect.p2p.observer;

public interface P2PPublisher {
    void add(P2PObserver observer);
    void delete(P2PObserver observer);

    void onConnectedObserver();
    void onDisconnectedObserver();
    void onFailedObserver();
    void onClosedObserver();
    void onErrorObserver(String description);
}
