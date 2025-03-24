package org.thermoweb.tasks.modular.application.task;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.thermoweb.tasks.modular.domain.task.Task;
import org.thermoweb.tasks.modular.domain.task.TaskRepository;

@Service
public class TaskAccessService {

    private final TaskRepository taskRepository;

    public TaskAccessService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Optional<Task> getTask(String taskId) {
        return taskRepository.findById(taskId);
    }
}
