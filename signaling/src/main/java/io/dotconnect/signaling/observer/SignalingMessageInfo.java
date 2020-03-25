package io.dotconnect.signaling.observer;

import io.dotconnect.signaling.callJni.SipMessage;

public class SignalingMessageInfo {

    private String senderEmail, message, chatId, messageType, fileType, fileUrl,
            messageId, messageDate, teamId, chatType;
    private int messageSeq;

    public SignalingMessageInfo(SipMessage sipMessage) {
        if (sipMessage!=null) {
            this.senderEmail = sipMessage.getFromId();
            this.message = sipMessage.getMessage();
//            this.chatId = sipMessage.getChatId();
//            this.messageType = sipMessage.getMessageType();
//            this.fileType = sipMessage.getFileType();
//            this.fileUrl = sipMessage.getFileUrl();
            this.messageId = sipMessage.getMessageId();
            this.messageDate = sipMessage.getMessageDate();
//            this.teamId = sipMessage.getTeamId();
//            this.chatType = sipMessage.getChatType();
//            try {
//                if (sipMessage.getMessageSeq()!=null)
//                    this.messageSeq = Integer.parseInt(sipMessage.getMessageSeq());
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
        }
    }

    public String getMessage() {
        return message;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageDate() {
        return messageDate;
    }
}
