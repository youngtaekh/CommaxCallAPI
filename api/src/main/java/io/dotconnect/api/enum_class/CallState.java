package io.dotconnect.api.enum_class;

public enum CallState {
    idle,
    sending,
    ringing,
    incoming,
    incomingConnectTry,
    incomingRTPConnecting,
    outgoingRTPConnecting,
    incomingConnecting,
    outgoingConnecting,
    calling,
    endTry
}
