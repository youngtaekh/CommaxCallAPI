package io.dotconnect.api.observer;

import io.dotconnect.signaling.observer.SignalingMessageInfo;

public class ApiMessageInfo {

    private String message, chatId, messageType, fileType, fileUrl,
            messageId, messageDate, teamId, chatType;
    private int messageSeq;

    public ApiMessageInfo(SignalingMessageInfo signalingMessageInfo) {
        this.message = signalingMessageInfo.getMessage();
        this.chatId = signalingMessageInfo.getChatId();
        this.messageType = signalingMessageInfo.getMessageType();
        this.fileType = signalingMessageInfo.getFileType();
        this.fileUrl = signalingMessageInfo.getFileUrl();
        this.messageId = signalingMessageInfo.getMessageId();
        this.messageDate = signalingMessageInfo.getMessageDate();
        this.teamId = signalingMessageInfo.getTeamId();
        this.chatType = signalingMessageInfo.getChatType();
        this.messageSeq = signalingMessageInfo.getMessageSeq();
    }

    public String getMessage() {
        return message;
    }

    public String getChatId() {
        return chatId;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getChatType() {
        return chatType;
    }

    public int getMessageSeq() {
        return messageSeq;
    }
}
