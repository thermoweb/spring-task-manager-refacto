package org.thermoweb.tasks.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.thermoweb.tasks.model.User;

@Service
public class UserProxyService {
    private final UserService legacyUserService;

    public UserProxyService(UserService legacyUserService) {
        this.legacyUserService = legacyUserService;
    }

    public List<User> allUsers() {
        return legacyUserService.allUsers();
    }

    public User createUser(User user) {
        return legacyUserService.createUser(user);
    }

    public Optional<User> findUser(Long userId) {
        return legacyUserService.findUser(userId);
    }
}
