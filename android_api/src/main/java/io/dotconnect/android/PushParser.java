package io.dotconnect.android;

import java.util.Map;

public class PushParser {
    private final static String FROM = "From";
    private final static String EVENT_TYPE = "eventType";
    private final static String TITLE = "title";
    private final static String MESSAGE = "message";
    private final static String CALLER = "caller";
    private final static String CALLEE = "callee";
    private final static String EVENT = "event";

    private final static String NAME = "commax";

    private EventType eventType;
    private String title, message, caller, callee, event;

    public enum EventType {
        call,
        cancel
    }

    public PushParser() {}

    private boolean check(String title) {
        return TITLE.equalsIgnoreCase(title);
    }

    public boolean setPushMap(Map<String, String> data) {
        if (data.containsKey(TITLE) && check(data.get(TITLE))) {
            eventType = EventType.valueOf(data.get(EVENT_TYPE));
            title = data.get(TITLE);
            message = data.get(MESSAGE);
            caller = data.get(CALLER);
            callee = data.get(CALLEE);
            event = data.get(EVENT);
            return true;
        } else {
            return false;
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
