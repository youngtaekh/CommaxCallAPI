package io.dotconnect.signaling.callJni;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nuno on 15. 8. 31.
 */
public class SipMessage {
    private static final String TAG = "SipMessage";

    private static final String EventCode = "eventCode";
    private static final String IMS = "ims";
    private static final String FromId = "fromId";
    private static final String CallKey = "callKey";
    private static final String StatusCode = "statusCode";
    private static final String Cause = "cause";
    private static final String Reason = "reason";
    private static final String Message = "message";
    private static final String ChatId = "chatId";
    private static final String MessageType = "messageType";
    private static final String FileType = "fileType";
    private static final String FileURL = "fileURL";
    private static final String MessageId = "messageId";
    private static final String MessageSeq = "messageSeq";
    private static final String MessageDate = "messageDate";
    private static final String CallType = "callType";
    private static final String Method = "method";
    private static final String TeamId = "teamId";
    private static final String ChatType = "chatType";
    private static final String SDP = "sdp";
    private static final String RemoteVideoCount = "trackCount";

    public static final String ID = "id";
    public static final String CallMessage = "callMessage";

    private boolean ims;
    private int eventCode, statusCode, cause, remoteVideoCount;
    private String fromId, callKey, reason, message, chatId,
            messageType, fileType, fileUrl, messageId, messageSeq,
            messageDate, callType, method, teamId, chatType, sdp;

    SipMessage(String jsonStr) {
        this.fromId = fromId;
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
            if (json.has(IMS))
                this.ims = json.getBoolean(IMS);
            if (json.has(CallKey))
                this.callKey = json.getString(CallKey);
            if (json.has(StatusCode))
                this.statusCode = json.getInt(StatusCode);
            if (json.has(Cause))
                this.cause = json.getInt(Cause);
            if (json.has(Reason))
                this.reason = json.getString(Reason);
            if (json.has(Message))
                this.message = json.getString(Message);
            if (json.has(ChatId))
                this.chatId = json.getString(ChatId);
            if (json.has(MessageType))
                this.messageType = json.getString(MessageType);
            if (json.has(FileType))
                this.fileType = json.getString(FileType);
            if (json.has(FileURL))
                this.fileUrl = json.getString(FileURL);
            if (json.has(MessageId))
                this.messageId = json.getString(MessageId);
            if (json.has(MessageSeq))
                this.messageSeq = json.getString(MessageSeq);
            if (json.has(MessageDate))
                this.messageDate = json.getString(MessageDate);
            if (json.has(CallType))
                this.callType = json.getString(CallType);
            if (json.has(Method))
                this.method = json.getString(Method);
            if (json.has(TeamId))
                this.teamId = json.getString(TeamId);
            if (json.has(ChatType))
                this.chatType = json.getString(ChatType);
            if (json.has(RemoteVideoCount))
                this.remoteVideoCount = json.getInt(RemoteVideoCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getEventCode() {
        return eventCode;
    }

    public boolean isIms() {
        return ims;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getCause() {
        return cause;
    }

    public int getRemoteVideoCount() {
        return remoteVideoCount;
    }

    public String getFromId() {
        return fromId;
    }

    public String getCallKey() {
        return callKey;
    }

    public String getReason() {
        return reason;
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

    public String getMessageSeq() {
        return messageSeq;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public String getCallType() {
        return callType;
    }

    public String getMethod() {
        return method;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getChatType() {
        return chatType;
    }

    public String getSdp() {
        return sdp;
    }
}
