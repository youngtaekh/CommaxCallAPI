package io.dotconnect.callapi.fcm;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import io.dotconnect.android.Register;
import io.dotconnect.callapi.MainActivity;

import java.util.Map;
import java.util.StringTokenizer;

public class ConnectFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> messageMap = remoteMessage.getData();
        String event = messageMap.get("eventType");
        String title = messageMap.get("title");
        String message = messageMap.get("message");

        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "eventType: " + event);

        String fromAccount = messageMap.get("caller");
        Log.d(TAG, "caller - " + fromAccount);
        Log.d(TAG, "callee - " + messageMap.get("callee"));

//        if(title.equals())
        if (event != null) {
            switch (event) {
                case "call" :
                    // Notify Activity of FCM push
                    if (!new Register().isRegistered()) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setAction(MainActivity.ACTION_INCOMING_CALL);
                        StringTokenizer tokenizer = new StringTokenizer(fromAccount, "@");
                        intent.putExtra(MainActivity.COUNTERPART_ACCOUNT, tokenizer.nextToken());
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(intent);
                    }
                    break;
                case "cancel":
                    // Notify Activity of FCM push
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_CANCEL_CALL);
                    this.sendBroadcast(intent);
                    break;
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // Notify Activity of FCM token
        Intent intent = new Intent("action_fcm_token");
        sendBroadcast(intent);
    }
}
