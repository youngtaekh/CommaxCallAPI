package io.dotconnect.p2p.observer;

public interface P2PObserver {
    void onConnected();
    void onDisconnected();
    void onFailed();
    void onClosed();
    void onError(String description);
}
