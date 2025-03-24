package org.thermoweb.tasks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.thermoweb.tasks.model.Task;
import org.thermoweb.tasks.model.User;
import org.thermoweb.tasks.modular.application.task.TaskAccessService;

@Service
public class TaskProxyService {
    private final TaskService legacyTaskService;
    private final TaskAccessService taskAccessService;

    public TaskProxyService(TaskService legacyTaskService, TaskAccessService taskAccessService) {
        this.legacyTaskService = legacyTaskService;
        this.taskAccessService = taskAccessService;
    }

    public List<Task> getAllTasks() {
        return legacyTaskService.getAllTasks();
    }

    public Optional<Task> getTaskById(String id) {
        return taskAccessService.getTask(id).map(t -> {
            Task task = new Task();
            task.setId(t.getId().value());
            task.setName(t.getTaskName());
            task.setDescription(t.getDescription());
            task.setStatus(t.getTaskStatus());
            Optional<User> assignee = Optional.ofNullable(t.getAssignee()).map(u -> {
                User user = new User();
                user.setId(u.getId().value());
                user.setName(u.getName());
                return user;
            });
            task.setAssignee(assignee.orElse(null));
            return task;
        });
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
