package org.thermoweb.tasks.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thermoweb.tasks.model.Task;
import org.thermoweb.tasks.model.User;
import org.thermoweb.tasks.service.TaskService;
import org.thermoweb.tasks.service.UserService;

@RestController
@RequestMapping("/rest/users")
public class UserController {
    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.allUsers().stream().map(UserDto::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreationRequest userCreationRequest) {
        User user = new User();
        user.setName(userCreationRequest.name());
        user.setEmail(userCreationRequest.email());
        try {
            User createUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{userId}/tasks")
    public List<TaskController.TaskDto> listUserTasks(@PathVariable Long userId) {
        User user = userService.findUser(userId).orElseThrow();
        return user.getTasks().stream().map(TaskController.TaskDto::toDto).toList();
    }

    @PostMapping("/{userId}:create-task")
    public ResponseEntity<TaskController.TaskDto> createTask(@PathVariable Long userId, @RequestBody TaskController.TaskCreationRequest taskCreationRequest) {
        User user = userService.findUser(userId).orElseThrow();
        Task task = new Task();
        task.setId(taskCreationRequest.id());
        task.setName(taskCreationRequest.name());
        task.setDescription(taskCreationRequest.description());
        task.setAssignee(user);
        try {
            Task createTask = taskService.create(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(TaskController.TaskDto.toDto(createTask));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public record UserDto(Long id, String name, String email) {
        public static UserDto toDto(User user) {
            if (user == null) {
                return null;
            }
            return new UserDto(user.getId(), user.getName(), user.getEmail());
        }
    }

    public record UserCreationRequest(String name, String email) {
    }
}
