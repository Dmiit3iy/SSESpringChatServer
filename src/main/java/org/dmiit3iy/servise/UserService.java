package org.dmiit3iy.servise;

import org.dmiit3iy.model.User;

public interface UserService {
    void add(User user);
    User get (long id);
}
