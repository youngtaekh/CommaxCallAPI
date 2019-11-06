package io.dotconnect.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallManager {
    private static CallManager instance;

//    private Map<String, SignalingCallInfo> callMap;
    private List<Call> callList;

    public static CallManager getInstance() {
        if (instance==null)
            instance = new CallManager();
        return instance;
    }

    private CallManager() {
//        callMap = new HashMap<>();
        callList = new ArrayList<>();
    }

    public int size() {
//        return callMap.size();
        return callList.size();
    }

    public void add(Call call) {
//        callMap.put(call.getCallId(), call);
        callList.add(call);
    }

//    public SignalingCallInfo get(String callId) {
//        return callMap.get(callId);
//    }
//
//    public void remove(String callId) {
//        callMap.remove(callId);
//    }

    public Call get() {
        if (callList.size()!=0)
            return callList.get(callList.size()-1);
        return null;
    }

    public void remove() {
        callList = new ArrayList<>();
    }
}
