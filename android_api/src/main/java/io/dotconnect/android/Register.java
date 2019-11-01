package io.dotconnect.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import io.dotconnect.android.observer.ConnectAction;
import io.dotconnect.android.util.AuthenticationUtil;
import io.dotconnect.android.util.Configuration;
import io.dotconnect.android.util.ConnectServer;
import io.dotconnect.signaling.callJni.CallCore;
import io.dotconnect.signaling.callJni.EventNotifier;
import io.dotconnect.signaling.util.NetworkUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

import static io.dotconnect.android.util.AuthenticationUtil.getUUID;
import static io.dotconnect.android.util.Configuration.*;
import static io.dotconnect.signaling.util.CertificationUtil.*;

public class Register {

    private static Register instance;

    static Register getInstance() {
        if (instance == null)
            instance = new Register();
        return instance;
    }

    private Register() {}

//    static Register getInstance() {
//        return instance;
//    }

    void release() {
        instance = null;
    }

    boolean isRegistered() {
        return CallCore.getInstance().isRegistered();
    }

    void start(Context context, String userId, String appId, String accessToken, String fcmToken, String tlsDomain) {
        if (isRegistered()) {
            //Observer
            ConnectAction.getInstance().onRegistrationSuccessObserver();
            //Listener
//            if (this.listener!=null) {
//                this.listener.onRegistrationSuccess();
//            }
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uuid", AuthenticationUtil.getUUID(context));
                jsonObject.put("deviceName", Build.MODEL);
                jsonObject.put("pushToken", fcmToken);
                jsonObject.put("osType", Configuration.OsType);
                jsonObject.put("osVersion", Build.VERSION.RELEASE);
                jsonObject.put("appVersion", BuildConfig.VERSION_NAME);
                new DeviceCheck(context, userId, appId, accessToken, tlsDomain)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Configuration.DEVICE_CHECK,
                                           jsonObject.toString(), accessToken);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeviceCheck extends AsyncTask<String, Void, String> {

        private Context mContext;
        private String userId;
        private String appId;
        private String accessToken;
        private String tlsDomain;

        DeviceCheck(Context context, String userId, String appId, String accessToken, String tlsDomain) {
            this.mContext = context;
            this.userId = userId;
            this.appId = appId;
            this.accessToken = accessToken;
            this.tlsDomain = tlsDomain;
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectServer.POST(params[0], params[1], params[2], null);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("asdf", result);
            try {
                deviceCheckJson(mContext, result, userId, appId, accessToken, tlsDomain);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void deviceCheckJson(Context context, String result, String userId, String appId, String accessToken, String tlsDomain) throws JSONException {

        JSONObject response = new JSONObject(result);
        JSONObject header = response.getJSONObject("header");
        if(header.getString("status").equals("success")){
            stop();
            sipStart(context, userId, appId, accessToken, tlsDomain);
        } else if(header.getString("status").equals("error")){
            //errCode check
        }
    }

    private void sipStart(Context context, String userId, String appId, String accessToken, String tlsDomain) {
        String domain = appId + "." + tlsDomain;
        generateCertification(context, domain);
        EventNotifier eventNotifier = EventNotifier.getInstance();

        CallCore callCore = CallCore.getInstance();

        callCore.createCoreServiceInstance(
                context,
                eventNotifier,
                -1,
                -1,
                0,
                context.getFilesDir().toString(),
                tlsDomain,
                registerDuration,
                getUUID(context),
                userId,
                makeSHA256(userId + appId),
                accessToken,
                "",
                domain,
                outboundProxyAddress,
                outboundProxyPort,
                coreVersion);

        String networkType = NetworkUtil.getNetworkType(context);
        String ipAddress = NetworkUtil.getIPAddress(networkType);

        callCore.start();
        callCore.startRegistration(networkType, ipAddress);
    }

    void start(Context context, String email, String password, String deviceId) {
        try {
            // encrypt password
            String userPassword = AuthenticationUtil.makeSHA256(password);
            String encryptedPassword    = "";
            try {
                encryptedPassword   = AuthenticationUtil.getEncryptedPasswordWithPasswordBasedSalt(userPassword);
            } catch (Exception e){
                Log.e("BackgroundService", "Exception occurred to encrypt user password!");
            }

            CallCore callCore = CallCore.getInstance();

            EventNotifier eventNotifier = EventNotifier.getInstance();
            StringTokenizer tokenizer = new StringTokenizer(email, "@");
            String id = tokenizer.nextToken();
            String domain = tokenizer.nextToken();

            generateCertification(context, domain);

            callCore.createCoreServiceInstance(
                    context,
                    eventNotifier,
                    -1,
                    -1,
                    0,
                    context.getFilesDir().toString(),
                    domain,
                    registerDuration,
                    deviceId,
                    id,
                    id,
                    accessToken,
                    encryptedPassword,
                    domain,
                    outboundProxyAddress,
                    outboundProxyPort,
                    coreVersion);

            String networkType = NetworkUtil.getNetworkType(context);
            String ipAddress = NetworkUtil.getIPAddress(networkType);

            callCore.start();
            callCore.startRegistration(networkType, ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stop() {
        CallCore callCore = CallCore.getInstance();
        callCore.stopRegistration();
    }

    private void createPKIFiles(String tlsDomain, String path, String certPath, String keyPath) {
        CallCore.getInstance().createPKIFiles(path + keyPath, path + certPath, tlsDomain);
    }

    private void generateCertification(Context context, String domain) {

        // generate X509Certificate
        copyAssets(context);
//        if (!certificateFile.exists() || !privateKeyFile.exists()) {
        createPKIFiles(domain,
                context.getFilesDir().getAbsolutePath() + "/",
//                Environment.getExternalStorageDirectory()+"/Typhone/Download/",
                "domain_cert_" + domain + ".pem",
                "domain_key_" + domain + ".pem");
        copyPEMFile(domain, outboundProxyAddress, context);
    }
}
