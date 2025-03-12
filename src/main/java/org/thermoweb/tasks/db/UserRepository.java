package org.thermoweb.tasks.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thermoweb.tasks.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
