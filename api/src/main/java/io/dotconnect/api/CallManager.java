package io.dotconnect.api;

import java.util.ArrayList;
import java.util.List;

public class CallManager {
    private static CallManager instance;

    public static CallManager getInstance() {
        if (instance==null)
            instance = new CallManager();
        return instance;
    }

    private CallManager() {
//        callMap = new HashMap<>();
        callList = new ArrayList<>();
    }

//    private Map<String, Call> callMap;
    private List<Call> callList;

    public void add(Call call) {
//        callMap.put(call.getCallId(), call);
        callList.add(call);
    }

//    public Call get(String callId) {
//        return callMap.get(callId);
//    }

    public Call get() {
        if (callList.size()!=0)
            return callList.get(callList.size()-1);
        return null;
    }

//    public void remove(String callId) {
//        callMap.remove(callId);
//    }

    public void remove() {
        callList = new ArrayList<>();
    }
}
