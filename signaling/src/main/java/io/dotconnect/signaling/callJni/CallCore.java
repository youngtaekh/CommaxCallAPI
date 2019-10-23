package io.dotconnect.signaling.callJni;

import android.content.Context;

public class CallCore
{
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("VoicelocoCore");
//        System.loadLibrary("jingle_peerconnection_so");
    }

    // member variables
    volatile  static CallCore instance;

    // constructor (only can be instantiated via singleton)
    private CallCore() {}
    public static CallCore getInstance() {
        if  (instance == null) {
            instance = new CallCore();
        }
        return  instance;
    }

    public static void destroyInstance(){
        instance = null;
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

    public native void setUserCredential
            (
                    String displayName,
                    String userId,
                    String userPassword
            );

    public native String getServerAddress();

    public native String getSipDomain();

    public native boolean isIncomingCall();

    public native boolean isUAC();

    public native boolean isCallPlaced();

    public native boolean isCallConnected();

    public native boolean isCallAccepted();

    public native boolean isCallEarly();

    public native boolean isRecvCallHold();

    // service start/stop methods
    public native void start();
    public native void stop();

    // call related methods
    public native void startRegistration(String networkType, String localIPAddress);
    public native void stopRegistration();
    public native void refreshRegistration(String networkType, String localIPAddress);
    public native void applyNetworkChange(String networkType, String localIPAddress, String activeInterfaceName);
    public native boolean isRegistered();
    public native boolean isExistCall();

    public native int makeCall(String target, String teamId, String sdp);
    public native int makeCallWithReason(String target, String teamId, String sdp, String reason, int cause);
    public native int makePSTNCall(String target, String teamId, String sdp);
    public native int makeVideoCall(String target, String teamId, String sdp);

    public native int acceptCall(String sdp);

    public native int rejectCall();
    public native int rejectCallWithReason(String reason, int cause);
    public native int rejectCallWithCode(int code);
    public native int hangupCall();
    public native int hangupCallWithReason(String reason, int cause);
    public native int cancelCall();
    public native int cancelCallWithReason(String reason, int cause);
    public native int muteCall();
    public native int unmuteCall();
    public native int holdCall();
    public native int unHoldCall();
    public native int busyOnIncomingCall();
    public native int temporarilyUnavailable();

    public native int createPKIFiles(String privateKeyFile, String certificateFile, String tlsDomain);
    public native int sendDtmf(int number);

    //MessageObserver
    public native int sendPlainMessage(String target, String teamId, String message, String chatType, String chatId, String messageId, String messageType);
    public native int sendOption(String target, String teamId, String message, String chatType, String chatId, String messageId, String messageType);
    public native int sendFileMessage(String target, String teamId, String message, String chatType, String chatId, String messageId, String messageType, String fileType, String fileUrl);
}