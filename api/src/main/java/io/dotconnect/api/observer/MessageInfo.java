package io.dotconnect.api.observer;

public class MessageInfo {

    private String message, chatId, messageType, fileType, fileUrl,
            messageId, messageDate, teamId, chatType;
    private int messageSeq;

    public MessageInfo(io.dotconnect.signaling.observer.Message message) {
        this.message = message.getMessage();
        this.chatId = message.getChatId();
        this.messageType = message.getMessageType();
        this.fileType = message.getFileType();
        this.fileUrl = message.getFileUrl();
        this.messageId = message.getMessageId();
        this.messageDate = message.getMessageDate();
        this.teamId = message.getTeamId();
        this.chatType = message.getChatType();
        this.messageSeq = message.getMessageSeq();
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
