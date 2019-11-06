package io.dotconnect.api.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ConnectSharedPreference {

    private static final String ID = "id";
    private static final String UUID = "uuid";

    private static ConnectSharedPreference uniqueInstance = null;
    private SharedPreferences sharedPreferences;

    private ConnectSharedPreference(Context context){
        sharedPreferences = context.getSharedPreferences("connect_db", Context.MODE_PRIVATE);
    }

    public static ConnectSharedPreference getInstance(Context context){
        if(uniqueInstance==null){
            uniqueInstance = new ConnectSharedPreference(context);
        }
        return uniqueInstance;
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void putValue(String key, Set<String> value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public void putValue(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putValue(String key, long value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putValue(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putValue(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putValue(String key, float value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key,"");
    }

    public int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

//    public int getInt1(String key){
//        return sharedPreferences.getInt(key, 1);
//    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public Long getLong(String key){
        return sharedPreferences.getLong(key, 0);
    }

    public float getFloat(String key){
        return sharedPreferences.getFloat(key, 0);
    }

    public Set<String> getStringSet(String key) {
        return sharedPreferences.getStringSet(key, new HashSet<String>());
    }

    //id
    public void setId(String id) {
        putValue(ID, id);
    }

    public String getId() {
        return getString(ID);
    }

    //uuid
    public void setUUID(String uuid) {
        putValue(UUID, uuid);
    }

    public String getUUID() {
        return getString(UUID);
    }
}
