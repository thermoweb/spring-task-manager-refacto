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

class UserProxyServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserProxyService userProxyService = new UserProxyService(new UserService(userRepository));

    @Test
    void should_create_user() {
        // GIVEN
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        User userToCreate = new User();
        userToCreate.setEmail("test@thermoweb.com");
        userToCreate.setName("name");

        // WHEN
        userProxyService.createUser(userToCreate);

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
        User user = new User();
        user.setEmail("test@thermoweb.com");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // WHEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userProxyService.createUser(user));

        // THEN
        assertEquals("User already exists", exception.getMessage());
    }
}