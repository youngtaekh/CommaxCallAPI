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
import static io.dotconnect.signaling.util.CertificationUtil.*;

public class Register {
    //    public static final String REST_URL = "https://www.voiceloco.com/api/v1.0";
    public static final String DOMAIN = ".api.voiceloco.com";

    private static final int registerDuration = 900;
    private static final String accessToken = "";
    private static final String outboundProxyAddress = "modev.voiceloco.com";
    private static final int outboundProxyPort = 5097;
    private static final String coreVersion = "1.0";

    private static Register instance;

    static Register getInstance() {
        if (instance == null)
            instance = new Register();
        return instance;
    }

//    static Register getInstance() {
//        return instance;
//    }

    public void release() {
        instance = null;
    }

    public boolean isRegistered() {
        return CallCore.getInstance().isRegistered();
    }

    public void start(Context context, String userId, String appId, String accessToken, String fcmToken) {
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
                new DeviceCheck(context, userId, appId, accessToken)
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

        DeviceCheck(Context context, String userId, String appId, String accessToken) {
            this.mContext = context;
            this.userId = userId;
            this.appId = appId;
            this.accessToken = accessToken;
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectServer.POST(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("asdf", result);
            try {
                deviceCheckJson(mContext, result, userId, appId, accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void deviceCheckJson(Context context, String result, String userId, String appId, String accessToken) throws JSONException {

        JSONObject response = new JSONObject(result);
        JSONObject header = response.getJSONObject("header");
        if(header.getString("status").equals("success")){
            stop();
            sipStart(context, userId, appId, accessToken);
        } else if(header.getString("status").equals("error")){
            //errCode check
        }
    }

    private void sipStart(Context context, String userId, String appId, String accessToken) {
        String domain = appId + DOMAIN;
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
                domain,
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
