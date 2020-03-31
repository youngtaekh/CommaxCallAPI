package io.dotconnect.api;

import io.dotconnect.api.enum_class.MessageDetail;
import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.signaling.callJni.CallCore;

import static io.dotconnect.api.util.AuthenticationUtil.getEncryptedHashId;

public class Message {
    int sendMessage(String target, String message, String deviceId,
                           MessageType messageType, MessageDetail messageDetail) {
        String messageId = getEncryptedHashId(deviceId + System.currentTimeMillis());
        switch (messageType) {
            case group:
            case userId:
            case uuid:
            case control:
                return CallCore.getInstance().sendMessage(target,
                        message, messageId, messageType.toString(), "");
            case cctv:
                return CallCore.getInstance().sendMessage
                        (target, message, messageId, messageType.toString(), messageDetail.toString());
            default:
                return -1;
        }
    }
}
