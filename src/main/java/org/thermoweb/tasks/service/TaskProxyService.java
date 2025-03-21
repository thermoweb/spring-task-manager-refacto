package org.thermoweb.tasks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.thermoweb.tasks.model.Task;
import org.thermoweb.tasks.model.User;

@Service
public class TaskProxyService {
    private final TaskService legacyTaskService;

    public TaskProxyService(TaskService legacyTaskService) {
        this.legacyTaskService = legacyTaskService;
    }

    public List<Task> getAllTasks() {
        return legacyTaskService.getAllTasks();
    }

    public Optional<Task> getTaskById(String id) {
        return legacyTaskService.getTask(id);
    }

    public Task create(Task task) {
        return legacyTaskService.create(task);
    }

    public void assign(Task task, User user) {
        legacyTaskService.assign(task, user);
    }

    public Optional<Task> findTask(String taskId) {
        return legacyTaskService.findTask(taskId);
    }

    public void update(Task task) {
        legacyTaskService.update(task);
    }
}
