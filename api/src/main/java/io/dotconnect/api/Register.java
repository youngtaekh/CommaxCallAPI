package io.dotconnect.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import io.dotconnect.api.observer.ConnectAction;
import io.dotconnect.api.util.APIConfiguration;
import io.dotconnect.api.util.AuthenticationUtil;
import io.dotconnect.api.util.ConnectServer;
import io.dotconnect.api.util.NetworkUtil;
import io.dotconnect.signaling.callJni.CallCore;
import io.dotconnect.signaling.callJni.EventNotifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.StringTokenizer;

import static io.dotconnect.api.util.APIConfiguration.*;
import static io.dotconnect.signaling.util.CertificationUtil.*;

public class Register {

    private static Register instance;

    static Register getInstance() {
        if (instance == null)
            instance = new Register();
        return instance;
    }

    private Register() {}

    void release() {
        instance = null;
    }

    boolean isRegistered() {
        return CallCore.getInstance().isRegistered();
    }

    void deviceCheck(String deviceId, String userId, String appId, String accessToken, String fcmToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", deviceId);
            jsonObject.put("huid", makeSHA256(userId + appId));
            jsonObject.put("deviceName", Build.MODEL);
            jsonObject.put("pushToken", fcmToken);
            jsonObject.put("osType", APIConfiguration.OsType);
            jsonObject.put("osVersion", Build.VERSION.RELEASE);
            jsonObject.put("appVersion", BuildConfig.VERSION_NAME);
            new OnlyDeviceCheck()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, APIConfiguration.DEVICE_CHECK,
                            jsonObject.toString(), accessToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void deviceUnRegistration(String deviceId, String accessToken) {
        new DeviceUnRegistration().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                DEVICE_UNREGISTRATION + deviceId, accessToken);
    }

    void start(Context context, String deviceId, String userId, String appId, String accessToken, String fcmToken) {
        start(context, deviceId, userId, appId, accessToken, fcmToken, DOMAIN);
    }

    void start(Context context, String deviceId, String userId, String appId, String accessToken, String fcmToken, String outboundProxy) {
        start(context, deviceId, userId, appId, accessToken, fcmToken, DOMAIN, outboundProxy);
    }

    void start(Context context, String deviceId, String userId, String appId, String accessToken, String fcmToken, String tlsDomain, String outboundProxy) {
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
                jsonObject.put("uuid", deviceId);
                jsonObject.put("huid", makeSHA256(userId + appId));
                jsonObject.put("deviceName", Build.MODEL);
                jsonObject.put("pushToken", fcmToken);
                jsonObject.put("osType", APIConfiguration.OsType);
                jsonObject.put("osVersion", Build.VERSION.RELEASE);
                jsonObject.put("appVersion", BuildConfig.VERSION_NAME);
                new DeviceCheck(context, deviceId, userId, appId, accessToken, tlsDomain, outboundProxy)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, APIConfiguration.DEVICE_CHECK,
                                           jsonObject.toString(), accessToken);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OnlyDeviceCheck extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return ConnectServer.POST(params[0], params[1], params[2], null);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(APP_NAME, result);
            try {
                JSONObject response = new JSONObject(result);
                JSONObject header = response.getJSONObject("header");
                if(header.getString("status").equals("success")) {
                    ConnectAction.getInstance().onDeviceRegistrationSuccessObserver();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeviceCheck extends AsyncTask<String, Void, String> {

        private Context mContext;
        private String deviceId;
        private String userId;
        private String appId;
        private String accessToken;
        private String tlsDomain;
        private String outboundProxy;

        DeviceCheck(Context context, String deviceId, String userId, String appId, String accessToken, String tlsDomain, String outboundProxy) {
            this.mContext = context;
            this.deviceId = deviceId;
            this.userId = userId;
            this.appId = appId;
            this.accessToken = accessToken;
            this.tlsDomain = tlsDomain;
            this.outboundProxy = outboundProxy;
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectServer.POST(params[0], params[1], params[2], null);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(APP_NAME, result);
            try {
                deviceCheckJson(mContext, result, deviceId, userId, appId, accessToken, tlsDomain, outboundProxy);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeviceUnRegistration extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return ConnectServer.DELETE(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(APP_NAME, result);
            try {
                JSONObject response = new JSONObject(result);
                JSONObject header = response.getJSONObject("header");
                if(header.getString("status").equals("success")){
                    ConnectAction.getInstance().onDeviceUnRegistrationSuccessObserver();
                } else if(header.getString("status").equals("error")){
                    //errCode check
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void deviceCheckJson(Context context, String result, String deviceId, String userId, String appId,
                                 String accessToken, String tlsDomain, String outboundProxy) throws JSONException {

        JSONObject response = new JSONObject(result);
        JSONObject header = response.getJSONObject("header");
        if(header.getString("status").equals("success")){
            stop();
            sipStart(context, deviceId, userId, appId, accessToken, tlsDomain, outboundProxy);
        } else if(header.getString("status").equals("error")){
            //errCode check
        }
    }

    void sipStart(Context context, String deviceId, String userId, String appId,
                          String accessToken, String tlsDomain, String outboundProxy) {
        if (appId == null || "".equals(appId)
                || tlsDomain == null || "".equals(tlsDomain)
                || outboundProxy == null || "".equals(outboundProxy))
            return;
//        String domain = appId + "." + tlsDomain;
        generateCertification(context, tlsDomain, outboundProxy);
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
                deviceId,
                userId,
                userId,
                accessToken,
                "aaaaaa",
                tlsDomain,
                outboundProxy,
                outboundProxyPort,
                coreVersion);

        String networkType = NetworkUtil.getNetworkType(context);
        String ipAddress = NetworkUtil.getIPAddress(networkType);

        callCore.start();
        callCore.startRegistration(networkType, ipAddress);
    }

    void stop() {
        CallCore callCore = CallCore.getInstance();
        callCore.stopRegistration();
    }

    private void createPKIFiles(String tlsDomain, String path, String certPath, String keyPath) {
        CallCore.getInstance().createPKIFiles(path + keyPath, path + certPath, tlsDomain);
    }

    private void generateCertification(Context context, String domain, String outboundProxy) {
        // generate X509Certificate
        copyAssets(context);
        createPKIFiles(domain,
                context.getFilesDir().getAbsolutePath() + "/",
                "domain_cert_" + domain + ".pem",
                "domain_key_" + domain + ".pem");
        copyPEMFile(domain, outboundProxy, context);
    }
}
