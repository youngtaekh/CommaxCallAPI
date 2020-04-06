package io.dotconnect.api;

import io.dotconnect.api.enum_class.MessageDetail;
import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.signaling.callJni.CallCore;

import static io.dotconnect.api.util.AuthenticationUtil.getEncryptedHashId;

public class Message {
    String getMessageId(String deviceId) {
        return getEncryptedHashId(deviceId + System.currentTimeMillis());
    }

    int sendMessage(String target, String message, String deviceId, MessageType messageType) {
        return sendMessage(target, message, deviceId, getMessageId(deviceId), messageType, MessageDetail.valueOf(""));
    }

    int sendMessage(String target, String message, String deviceId,
                    MessageType messageType, MessageDetail messageDetail) {
        return sendMessage(target, message, deviceId, getMessageId(deviceId), messageType, messageDetail);
    }

    int sendMessage(String target, String message, String deviceId, String messageId,
                    MessageType messageType, MessageDetail messageDetail) {
        switch (messageType) {
            case group:
            case userId:
            case uuid:
            case control:
            case cctv:
                return CallCore.getInstance().sendMessage
                        (target, message, messageId, messageType.toString(), messageDetail.toString());
            default:
                return -1;
        }
    }
}
