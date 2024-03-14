package org.dmiit3iy.servise;

import org.dmiit3iy.model.User;
import org.dmiit3iy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User does not exists!"));
    }
}