package org.dmiit3iy.servise;

import org.dmiit3iy.model.User;

import java.util.List;

public interface UserService {
    void add(User user);
    User get (long id);
    User getByLoginAndPassword(String login, String password);
    User delete(long id);
    List<User> getOnlineUsers();
}
