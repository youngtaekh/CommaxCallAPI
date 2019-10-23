package io.dotconnect.signaling.observer;

import io.dotconnect.signaling.callJni.SipMessage;

public class Message {
    public static final String SENDER_EMAIL = "senderEmail";
    public static final String MESSAGE = "message";
    public static final String CHAT_ID = "chatId";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String FILE_TYPE = "fileType";
    public static final String FILE_URL = "fileURL";
    public static final String MESSAGE_ID = "messageId";
    public static final String MESSAGE_SEQ = "messageSeq";
    public static final String MESSAGE_DATE = "messageDate";
    public static final String TEAM_ID = "teamId";
    public static final String CHAT_TYPE = "chatType";

    private String senderEmail, message, chatId, messageType, fileType, fileUrl,
            messageId, messageDate, teamId, chatType;
    private int messageSeq;

    public Message(SipMessage sipMessage) {
        if (sipMessage!=null) {
            this.senderEmail = sipMessage.getFromId();
            this.message = sipMessage.getMessage();
            this.chatId = sipMessage.getChatId();
            this.messageType = sipMessage.getMessageType();
            this.fileType = sipMessage.getFileType();
            this.fileUrl = sipMessage.getFileUrl();
            this.messageId = sipMessage.getMessageId();
            this.messageDate = sipMessage.getMessageDate();
            this.teamId = sipMessage.getTeamId();
            this.chatType = sipMessage.getChatType();
            try {
                this.messageSeq = Integer.parseInt(sipMessage.getMessageSeq());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public String getSenderEmail() {
        return senderEmail;
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
