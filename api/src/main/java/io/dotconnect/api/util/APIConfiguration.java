package io.dotconnect.api.util;

import io.dotconnect.api.BuildConfig;

public class APIConfiguration {
    public static final String REST_URL = "https://commax.dotconnect-api.io:4435/api/v1.0";
    public static final String APP_NAME = "Commax API";
    public static final String OsType = "Android";
    public static final String API_VERSION = "/v1.0";
    public static final String DEVICE_CHECK = "/users/devices";
    public static final String DEVICE_UNREGISTRATION = "/users/devices/";

    public static final int registerDuration = 350;
    public static final String accessToken = "";
    public static final String DOMAIN = "commax.dotconnect-api.io";
    public static final String outboundProxyAddress = "commax.dotconnect-api.io";
    public static final int outboundProxyPort = 5071;
    public static final String coreVersion = BuildConfig.VERSION_NAME;
}
