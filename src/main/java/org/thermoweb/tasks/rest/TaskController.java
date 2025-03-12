package org.thermoweb.tasks.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thermoweb.tasks.model.Task;
import org.thermoweb.tasks.model.TaskStatus;
import org.thermoweb.tasks.model.User;
import org.thermoweb.tasks.service.TaskService;
import org.thermoweb.tasks.service.UserService;

@RestController
@RequestMapping("/rest/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks().stream().map(TaskDto::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskCreationRequest taskCreationRequest) {
        Task task = new Task();
        task.setId(taskCreationRequest.id);
        task.setName(taskCreationRequest.name);
        task.setDescription(taskCreationRequest.description);
        try {
            Task createdTask = taskService.create(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(TaskDto.toDto(createdTask));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(TaskDto::toDto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}:assign")
    public void assignTask(@PathVariable(name = "id") String taskId, @RequestBody TaskAssignRequest taskAssignRequest) {
        User user = userService.findUser(taskAssignRequest.userId()).orElseThrow();
        Task task = taskService.findTask(taskId).orElseThrow();
        taskService.assign(task, user);
    }

    @PostMapping("/{id}:cancel")
    public void cancelTask(@PathVariable(name = "id") String taskId) {
        Task task = taskService.findTask(taskId).orElseThrow();
        task.setAssignee(null);
        task.setStatus(TaskStatus.CANCELLED);
        taskService.update(task);
    }

    @PostMapping("/{id}:complete")
    public void completeTask(@PathVariable(name = "id") String taskId) {
        Task task = taskService.findTask(taskId).orElseThrow();
        task.setAssignee(null);
        task.setStatus(TaskStatus.COMPLETED);
        taskService.update(task);
    }

    @PatchMapping("/{id}")
    public void updateTask(@PathVariable(name = "id") String taskId, @RequestBody TaskUpdateRequest taskDto) {
        TaskDto existingTask = taskService.findTask(taskId).map(TaskDto::toDto).orElseThrow();
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setName(taskDto.name == null ? existingTask.name : taskDto.name);
        updatedTask.setDescription(taskDto.description == null ? existingTask.description : taskDto.description);
        updatedTask.setStatus(taskDto.status == null ? existingTask.taskStatus : taskDto.status);
        taskService.update(updatedTask);
    }

    public record TaskUpdateRequest(String name, String description, TaskStatus status) {

    }

    public record TaskCreationRequest(String id, String name, String description) {
    }

    public record TaskAssignRequest(Long userId) {
    }

    public record TaskDto(String id, String name, String description, TaskStatus taskStatus,
                          UserController.UserDto assignee) {
        public static TaskDto toDto(Task task) {
            return new TaskDto(task.getId(), task.getName(), task.getDescription(), task.getStatus(), UserController.UserDto.toDto(task.getAssignee()));
        }
    }
}
