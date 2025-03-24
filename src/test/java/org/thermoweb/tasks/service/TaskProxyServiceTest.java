package org.thermoweb.tasks.service;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thermoweb.tasks.db.TaskRepository;
import org.thermoweb.tasks.model.Task;
import org.thermoweb.tasks.model.TaskStatus;
import org.thermoweb.tasks.model.User;
import org.thermoweb.tasks.modular.application.task.TaskAccessService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TaskProxyServiceTest {
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final org.thermoweb.tasks.modular.domain.task.TaskRepository newTaskRepository = mock(org.thermoweb.tasks.modular.domain.task.TaskRepository.class);
    private final TaskAccessService taskAccessService = new TaskAccessService(newTaskRepository);
    private final TaskProxyService taskProxyService = new TaskProxyService(new TaskService(taskRepository), taskAccessService);

    @Test
    void should_create_task() {
        // GIVEN
        Task task = getTask();
        String taskId = task.getId();
        given(taskRepository.findById(taskId)).willReturn(Optional.empty());
        given(taskRepository.save(any())).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        // WHEN
        Task createdTask = taskProxyService.create(task);

        // THEN
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        assertNotNull(createdTask);
        then(taskRepository)
                .should()
                .save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertEquals(task.getId(), savedTask.getId());
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(TaskStatus.OPEN, savedTask.getStatus());
    }

    @Test
    void should_throw_exception_when_task_already_exists() {
        // GIVEN
        Task task = getTask();
        given(taskRepository.findById(task.getId())).willReturn(Optional.of(new Task()));


        // WHEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskProxyService.create(task));

        // THEN
        assertEquals("Task with id " + task.getId() + " already exists", exception.getMessage());
    }

    @Test
    void should_assign_task_to_user() {
        // GIVEN
        Task task = getTask();
        task.setStatus(TaskStatus.OPEN);
        User user = getUser();
        given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

        // WHEN
        taskProxyService.assign(task, user);

        // THEN
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        then(taskRepository)
                .should()
                .save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertEquals(task.getId(), savedTask.getId());
        assertEquals(TaskStatus.TODO, savedTask.getStatus());
        assertSame(user, savedTask.getAssignee());
    }

    @Test
    void should_throw_exception_when_task_is_not_assignable_to_user() {
        // GIVEN
        Task task = getTask();
        task.setStatus(TaskStatus.COMPLETED);
        User user = getUser();
        given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

        // WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskProxyService.assign(task, user));

        // THEN
        assertEquals("only open tasks could be assigned", exception.getMessage());
    }

    @Test
    void should_update_task() {
        // GIVEN
        Task existingTask = getTask();
        existingTask.setStatus(TaskStatus.OPEN);
        given(taskRepository.findById(existingTask.getId())).willReturn(Optional.of(existingTask));
        Task taskToUpdate = getTask();
        taskToUpdate.setId(existingTask.getId());
        taskToUpdate.setStatus(TaskStatus.OPEN);
        taskToUpdate.setName("updated name");

        // WHEN
        taskProxyService.update(taskToUpdate);

        // THEN
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        then(taskRepository)
                .should()
                .save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertEquals(existingTask.getId(), savedTask.getId());
        assertEquals(TaskStatus.OPEN, savedTask.getStatus());
        assertEquals("updated name", savedTask.getName());
    }

    @Test
    void should_throw_exception_when_task_status_update_is_not_allowed() {
        // GIVEN
        Task existingTask = getTask();
        existingTask.setStatus(TaskStatus.COMPLETED);
        given(taskRepository.findById(existingTask.getId())).willReturn(Optional.of(existingTask));
        Task taskToUpdate = getTask();
        taskToUpdate.setId(existingTask.getId());
        taskToUpdate.setStatus(TaskStatus.TODO);
        taskToUpdate.setName("updated name");

        // WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskProxyService.update(taskToUpdate));

        // THEN
        assertEquals("Task status TODO is not authorized to change status of task COMPLETED", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_task_status_update_is_not_allowed_without_assignee() {
        // GIVEN
        Task existingTask = getTask();
        existingTask.setStatus(TaskStatus.OPEN);
        given(taskRepository.findById(existingTask.getId())).willReturn(Optional.of(existingTask));
        Task taskToUpdate = getTask();
        taskToUpdate.setId(existingTask.getId());
        taskToUpdate.setStatus(TaskStatus.TODO);
        taskToUpdate.setName("updated name");

        // WHEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskProxyService.update(taskToUpdate));

        // THEN
        assertEquals("Task with new status TODO is not assigned", exception.getMessage());
    }

    private static User getUser() {
        User user = new User();
        user.setEmail("test@thermoweb.com");
        user.setName("name");
        user.setId(1L);
        return user;
    }


    private static Task getTask() {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setName("test task");
        task.setDescription("do unit test on task creation");
        return task;
    }
}