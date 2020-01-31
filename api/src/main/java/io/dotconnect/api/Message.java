package io.dotconnect.api;

import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.signaling.callJni.CallCore;

import static io.dotconnect.api.util.AuthenticationUtil.getEncryptedHashId;

public class Message {
    int sendMessage(String target, String teamId, String message, String deviceId,
                           String chatType, String chatId, MessageType messageType) {
        String messageId = getEncryptedHashId(deviceId + String.valueOf(System.currentTimeMillis()));
        switch (messageType) {
            case normal:
            case one:
            case conf:
            case group:
            case userId:
            case uuid:
                return CallCore.getInstance().sendPlainMessage(target, teamId,
                        message, chatType, chatId, messageId, messageType.toString());
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
                return CallCore.getInstance().sendOption(target, teamId, message,
                        chatType, chatId, messageId, messageType.toString());
            default:
                return -1;
        }
    }

    int sendFile(String target, String teamId, String message, String chatType, String deviceId,
                        String chatId, MessageType messageType, String fileType, String fileUrl) {
        String messageId = getEncryptedHashId(deviceId + String.valueOf(System.currentTimeMillis()));
        return CallCore.getInstance().sendFileMessage(target, teamId, message,
                chatType, chatId, messageId, messageType.toString(), fileType, fileUrl);
    }
}
