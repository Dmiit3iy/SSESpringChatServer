package org.dmiit3iy.event;

import org.dmiit3iy.model.Message;
import org.springframework.context.ApplicationEvent;


import java.util.StringJoiner;

public class MessageEvent extends ApplicationEvent {

    private final Event event;

    public MessageEvent(Object source, String user, Message message) {
        super(source);
        this.event = new Event(user, message);
    }

    public Event getEvent() {
        return event;
    }

    public static class Event {

        private final String login;
        private final Message message;

        public Event(String user, Message message) {
            this.login = user;
            this.message = message;
        }

        public String getLogin() {
            return login;
        }

        public Message getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Event.class.getSimpleName() + "[", "]")
                    .add("login='" + login + "'")
                    .add("message='" + message.getMessage() + "'")
                    .toString();
        }

    }
}
