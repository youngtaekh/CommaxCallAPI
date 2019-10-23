package io.dotconnect.android;

import java.util.HashMap;
import java.util.Map;

public class CallManager {
    private static CallManager instance;

    public static CallManager getInstance() {
        if (instance==null)
            instance = new CallManager();
        return instance;
    }

    private CallManager() {
        callMap = new HashMap<>();
    }

    private Map<String, Call> callMap;

    public void add(Call call) {
        callMap.put(call.getCallId(), call);
    }

    public Call get(String callId) {
        return callMap.get(callId);
    }

    public void remove(String callId) {
        callMap.remove(callId);
    }
}
