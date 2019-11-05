package io.dotconnect.android.util;

import io.dotconnect.android.BuildConfig;

public class Configuration {
    public static final String APP_NAME = "Commax API";
    public static final String OsType = "Android";
    public static final String API_VERSION = "/v1.0";
    public static final String DEVICE_CHECK = "/users/devices";
    public static final String DEVICE_UNREGISTRATION = "/users/devices/";

    public static final int registerDuration = 350;
    public static final String accessToken = "";
    public static final String DOMAIN = "commax.dot-connect.io";
    public static final String outboundProxyAddress = "commax.dot-connect.io";
    public static final int outboundProxyPort = 5071;
    public static final String coreVersion = BuildConfig.VERSION_NAME;
}
