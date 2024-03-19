package org.dmiit3iy.servise;

import org.dmiit3iy.event.OnlineEvent;
import org.dmiit3iy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class OnlineServiceImpl implements OnlineService {
    private ApplicationEventPublisher eventPublisher;
    private UserService userService;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void add(List<User> userList) {
        eventPublisher.publishEvent(new OnlineEvent(this, userList));
    }
}
