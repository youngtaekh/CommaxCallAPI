package io.dotconnect.api;

public class CallManager {
    private static CallManager instance;

    private Call call;

    public static CallManager getInstance() {
        if (instance==null)
            instance = new CallManager();
        return instance;
    }

    private CallManager() {}

    int size() {
        return call==null?0:1;
    }

    public void add(Call call) {
        this.call = call;
    }

    public Call get() {
        if (size()!=0)
            return call;
        return null;
    }

    void remove() {
        call = null;
    }
}
