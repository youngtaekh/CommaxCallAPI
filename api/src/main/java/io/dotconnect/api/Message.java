package io.dotconnect.api;

import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.signaling.callJni.CallCore;

import static io.dotconnect.api.util.AuthenticationUtil.getEncryptedHashId;

public class Message {
    int sendMessage(String target, String message, String deviceId,
                           MessageType messageType) {
        String messageId = getEncryptedHashId(deviceId + System.currentTimeMillis());
        switch (messageType) {
            case normal:
            case one:
            case conf:
            case group:
            case userId:
            case uuid:
                return CallCore.getInstance().sendPlainMessage(target,
                        message, messageId, messageType.toString());
            case quit:
            case create:
            case invite:
            case change:
            case changeExplain:
            case notice:
            case read:
            case joinChannel:
            case deleteChannel:
            case linkParsed:
            case cctv:
            case control:
                return CallCore.getInstance().sendOption(target, message, messageId, messageType.toString());
            default:
                return -1;
        }
    }
}
