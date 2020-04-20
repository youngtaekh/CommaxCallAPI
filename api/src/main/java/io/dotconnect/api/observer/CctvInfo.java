package io.dotconnect.api.observer;

import org.json.JSONException;
import org.json.JSONObject;

public class CctvInfo {
    public static final String DEVICES = "devices";
    public static final String DEVICE_COUNT = "deviceCount";
    public static final String TYPE = "type";
    public static final String KEY = "key";
    public static final String NAME = "name";

    private String type;
    private String key;
    private String name;

    public CctvInfo(JSONObject json) {
        this.type = checkValue(json, TYPE);
        this.key = checkValue(json, KEY);
        this.name = checkValue(json, NAME);
    }

    private String checkValue(JSONObject json, String key) {
        if (json.has(key)) {
            try {
                return json.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CctvInfo{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
