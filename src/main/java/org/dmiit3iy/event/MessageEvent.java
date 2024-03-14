package org.dmiit3iy.event;

import org.springframework.context.ApplicationEvent;


import java.util.StringJoiner;

public class MessageEvent extends ApplicationEvent {

    private final Event event;

    public MessageEvent(Object source, String user, String message) {
        super(source);
        this.event = new Event(user, message);
    }

    public Event getEvent() {
        return event;
    }

    public static class Event {

        private final String login;
        private final String message;

        public Event(String user, String message) {
            this.login = user;
            this.message = message;
        }

        public String getLogin() {
            return login;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Event.class.getSimpleName() + "[", "]")
                    .add("login='" + login + "'")
                    .add("message='" + message + "'")
                    .toString();
        }

    }
}
