package org.dmiit3iy.servise;

import org.dmiit3iy.model.User;
import org.dmiit3iy.repository.SseEmitters;
import org.dmiit3iy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private SseEmitters sseEmitters;

    @Autowired
    public void setSseEmitters(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    @Override
    public void add(User user) {
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("This user is already added");
        }
    }


    @Override
    public User get(long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User does not exists!"));
    }

    @Override
    public User getByLoginAndPassword(String login, String password) {
        return userRepository.findByLoginAndPassword(login, password).orElseThrow(() -> new IllegalArgumentException("User does not exists!"));
    }

    @Override
    public User delete(long id) {
        User user = get(id);
        userRepository.deleteById(id);
        return user;
    }

    @Override
    public List<User> getOnlineUsers() {
        List<User> userList = new ArrayList<>();
        List<Long> longList = sseEmitters.getOnlineUsers();
        for (Long x : longList) {
            userList.add(get(x));
        }
        return userList;
    }
}
