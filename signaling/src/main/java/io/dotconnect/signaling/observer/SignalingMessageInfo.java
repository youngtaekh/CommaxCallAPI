package io.dotconnect.signaling.observer;

import io.dotconnect.api.enum_class.MessageDetail;
import io.dotconnect.api.enum_class.MessageType;
import io.dotconnect.signaling.callJni.SipMessage;

public class SignalingMessageInfo {

    private String senderEmail, message, messageId;
    private MessageType messageType;
    private MessageDetail messageDetail;

    public SignalingMessageInfo(SipMessage sipMessage) {
        if (sipMessage!=null) {
            this.senderEmail = sipMessage.getFromId();
            this.message = sipMessage.getMessage();
            this.messageId = sipMessage.getMessageId();
            this.messageType = sipMessage.getMessageType();
            if (sipMessage.getMessageDetail()!=null)
                this.messageDetail = MessageDetail.valueOf(sipMessage.getMessageDetail());
        }
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public MessageDetail getMessageDetail() {
        return messageDetail;
    }
}
