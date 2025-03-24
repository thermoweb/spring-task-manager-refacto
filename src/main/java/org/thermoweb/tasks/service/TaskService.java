package org.thermoweb.tasks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thermoweb.tasks.db.TaskRepository;
import org.thermoweb.tasks.model.Task;
import org.thermoweb.tasks.model.TaskStatus;
import org.thermoweb.tasks.model.User;

@Service
@Deprecated
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Task create(Task task) {
        Optional<Task> existingTask = repository.findById(task.getId());
        if (existingTask.isPresent()) {
            throw new IllegalArgumentException("Task with id " + task.getId() + " already exists");
        }
        task.setStatus(TaskStatus.OPEN);
        return repository.save(task);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void assign(Task task, User user) {
        Task existingTask = repository.findById(task.getId()).orElseThrow();
        if (existingTask.getStatus() != TaskStatus.OPEN) {
            throw new IllegalArgumentException("only open tasks could be assigned");
        }
        existingTask.setAssignee(user);
        existingTask.setStatus(TaskStatus.TODO);
        repository.save(existingTask);
    }

    @Transactional(readOnly = true)
    public Optional<Task> findTask(String taskId) {
        return repository.findById(taskId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(Task task) {
        Task existingTask = repository.findById(task.getId()).orElseThrow();
        if (existingTask.getStatus() != task.getStatus()) {
            validateStatusChanges(existingTask.getStatus(), task);
        }
        repository.save(task);
    }

    private void validateStatusChanges(TaskStatus oldStatus, Task newTask) {
        TaskStatus newStatus = newTask.getStatus();
        if (newStatus != oldStatus && !oldStatus.isAuthorizedNewStatus(newStatus)) {
            throw new IllegalArgumentException("Task status " + newStatus + " is not authorized to change status of task " + oldStatus);
        }
        if (List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS).contains(newStatus) && newTask.getAssignee() == null) {
            throw new IllegalArgumentException("Task with new status " + newStatus + " is not assigned");
        }
    }
}
