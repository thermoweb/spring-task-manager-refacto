package org.thermoweb.tasks.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thermoweb.tasks.db.UserRepository;
import org.thermoweb.tasks.model.User;

@Service
@Deprecated
public class UserService {
    private final UserRepository userRepository;
    private final Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        if (!emailPattern.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email");
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }
}
