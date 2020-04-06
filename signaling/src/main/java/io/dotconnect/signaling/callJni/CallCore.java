package io.dotconnect.signaling.callJni;

import android.content.Context;

public class CallCore
{
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("VoicelocoCore");
    }

    // member variables
    private volatile static CallCore instance;

    // constructor (only can be instantiated via singleton)
    private CallCore() {}
    public static CallCore getInstance() {
        if  (instance == null) {
            instance = new CallCore();
        }
        return  instance;
    }

    // native methods
    public native void createCoreServiceInstance
    (
            Context ctx,
            EventNotifier eventNotifier,
            int udpPort,
            int tcpPort,
            int tlsPort,
            String sipCertFilePath,
            String tlsDomain,
            int registerDuration,
            String guid,
            String displayName,
            String userId,
            String accessToken,
            String userPassword,
            String sipDomain,
            String outboundProxyAddress,
            int outboundProxyPort,
            String version
    );

    // service start/stop methods
    public native void start();
    public native void stop();

    // call related methods
    public native void startRegistration(String networkType, String localIPAddress);
    public native void stopRegistration();
    public native void applyNetworkChange(String networkType, String localIPAddress, String sdp);
    public native boolean isRegistered();

    public native int makeCall(String target, String teamId, String sdp);
    public native int acceptCall(String sdp);

    public native int rejectCall();
    public native int hangupCall();
    public native int cancelCall();

    public native int createPKIFiles(String privateKeyFile, String certificateFile, String tlsDomain);

    //MessageObserver
    public native int sendMessage(String target, String message, String messageId, String messageType, String messageDetail);
}