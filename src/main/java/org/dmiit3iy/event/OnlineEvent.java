package org.dmiit3iy.event;


import org.dmiit3iy.model.User;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class OnlineEvent extends ApplicationEvent {
    private final Event event;

    public OnlineEvent(Object source, List<User> userList) {
        super(source);
        this.event = new Event(userList);
    }

    public Event getEvent() {
        return event;
    }

    public static class Event {

        private List<User> listOnlineUsers = new ArrayList<>();

        public List<User> getListOnlineUsers() {
            return listOnlineUsers;
        }

        public Event(List<User> listOnlineUsers) {
            this.listOnlineUsers = listOnlineUsers;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", MessageEvent.Event.class.getSimpleName() + "[", "]")
                    .add("online users='" + listOnlineUsers + "'")
                    .toString();
        }

    }
}
