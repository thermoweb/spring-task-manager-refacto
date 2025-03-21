package org.thermoweb.tasks.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thermoweb.tasks.db.UserRepository;
import org.thermoweb.tasks.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);

    @Test
    void should_create_user() {
        // GIVEN
        UserService userService = new UserService(userRepository);
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        User userToCreate = new User();
        userToCreate.setEmail("test@thermoweb.com");
        userToCreate.setName("name");

        // WHEN
        userService.createUser(userToCreate);

        // THEN
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository)
                .should()
                .save(userCaptor.capture());
        User user = userCaptor.getValue();
        assertEquals("name", user.getName());
        assertEquals("test@thermoweb.com", user.getEmail());
    }

    @Test
    void should_throw_exception_when_user_already_exists() {
        // GIVEN
        UserService userService = new UserService(userRepository);
        User user = new User();
        user.setEmail("test@thermoweb.com");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // WHEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));

        // THEN
        assertEquals("User already exists", exception.getMessage());
    }
}