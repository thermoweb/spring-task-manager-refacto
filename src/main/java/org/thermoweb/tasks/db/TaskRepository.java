package org.thermoweb.tasks.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thermoweb.tasks.model.Task;

public interface TaskRepository extends JpaRepository<Task, String> {
}
