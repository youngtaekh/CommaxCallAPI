package io.dotconnect.signaling.callJni;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import io.dotconnect.api.enum_class.MessageType;

/**
 * Created by nuno on 15. 8. 31.
 */
public class SipMessage {
    private static final String TAG = "SipMessage";

    private static final String EventCode = "eventCode";
    private static final String IMS = "ims";
    private static final String FromId = "fromId";
    private static final String StatusCode = "statusCode";
    private static final String Cause = "cause";
    private static final String Reason = "reason";
    private static final String Message = "message";
    private static final String KeyMessageType = "messageType";
    private static final String MessageId = "messageId";
    private static final String MessageDetail = "messageDetail";
    private static final String Method = "method";
    private static final String SDP = "sdp";

    public static final String ID = "id";
    public static final String CallMessage = "callMessage";

    private int eventCode, statusCode, cause;
    private String fromId, reason, message, messageId, messageDetail, method, sdp;
    private MessageType messageType;

    SipMessage(String fromId, String sdp, String jsonStr) {
        this.fromId = fromId;
        if (this.fromId!=null) {
            Log.d(TAG, "fromId - " + this.fromId);
        }
        this.sdp = sdp;
        if (this.sdp!=null) {
            Log.d(TAG, "sdp - " + this.sdp);
        }
        try {
            Log.d(TAG, jsonStr);
            JSONObject json = new JSONObject(jsonStr);
            if (json.has(FromId))
                this.fromId = json.getString(FromId);
            if (json.has(SDP))
                this.sdp = json.getString(SDP);
            if (json.has(EventCode))
                this.eventCode = json.getInt(EventCode);
            if (json.has(StatusCode))
                this.statusCode = json.getInt(StatusCode);
            if (json.has(Cause))
                this.cause = json.getInt(Cause);
            if (json.has(Reason))
                this.reason = json.getString(Reason);
            if (json.has(Message))
                this.message = json.getString(Message);
            if (json.has(KeyMessageType))
                this.messageType = MessageType.valueOf(json.getString(KeyMessageType));
            if (json.has(MessageId))
                this.messageId = json.getString(MessageId);
            if (json.has(MessageDetail))
                this.messageDetail = json.getString(MessageDetail);
            if (json.has(Method))
                this.method = json.getString(Method);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getEventCode() {
        return eventCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getCause() {
        return cause;
    }

    public String getFromId() {
        return fromId;
    }

    public String getReason() {
        return reason;
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

    public String getMessageDetail() {
        return messageDetail;
    }

    public String getMethod() {
        return method;
    }

    public String getSdp() {
        return sdp;
    }
}
