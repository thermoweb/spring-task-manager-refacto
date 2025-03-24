package org.thermoweb.tasks.modular.infrastructure.task;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.thermoweb.tasks.modular.domain.task.Assignee;
import org.thermoweb.tasks.modular.domain.task.Task;
import org.thermoweb.tasks.modular.domain.task.TaskRepository;

@Repository
public class JpaTaskRepository implements TaskRepository {

    private final org.thermoweb.tasks.db.TaskRepository repository;

    public JpaTaskRepository(org.thermoweb.tasks.db.TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Task> findById(String id) {
        return repository.findById(id)
                .map(JpaTaskRepository::map);
    }

    private static Task map(org.thermoweb.tasks.model.Task task) {
        return new Task(
                new Task.TaskId(task.getId()),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                Optional.ofNullable(task.getAssignee()).map(JpaTaskRepository::map).orElse(null)
        );
    }

    private static Assignee map(org.thermoweb.tasks.model.User user) {
        return new Assignee(new Assignee.UserId(user.getId()), user.getName());
    }
}
