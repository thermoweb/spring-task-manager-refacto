package org.thermoweb.tasks.rest;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = BEFORE_TEST_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_task() throws Exception {
        TaskController.TaskCreationRequest taskCreationRequest = new TaskController.TaskCreationRequest("new-task-id", "task name", "task description");
        mockMvc.perform(post("/rest/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(taskCreationRequest.id()))
                .andExpect(jsonPath("$.name").value(taskCreationRequest.name()))
                .andExpect(jsonPath("$.description").value(taskCreationRequest.description()))
                .andExpect(jsonPath("$.taskStatus").value("OPEN"));
    }

    @Test
    void should_not_create_task_if_task_already_exists() throws Exception {
        TaskController.TaskCreationRequest taskCreationRequest = new TaskController.TaskCreationRequest("already-existing-task", "task name", "task description");
        mockMvc.perform(post("/rest/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_assign_task_to_user() throws Exception {
        TaskController.TaskAssignRequest taskAssignRequest = new TaskController.TaskAssignRequest(1L);
        String taskId = "opened-task";
        mockMvc.perform(post("/rest/tasks/{taskId}:assign", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskAssignRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void should_list_all_tasks() throws Exception {
        String response = mockMvc.perform(get("/rest/tasks"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<TaskController.TaskDto> tasks = Arrays.asList(objectMapper.readValue(response, TaskController.TaskDto[].class));

        assertFalse(tasks.isEmpty());
        assertTrue(tasks.size() >= 2);
    }

    @Test
    void should_update_task() throws Exception {
        String taskToUpdateId = "task-to-update";
        TaskController.TaskUpdateRequest update = new TaskController.TaskUpdateRequest("updated task", "new task name", null);
        mockMvc.perform(patch("/rest/tasks/{id}", taskToUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
        String response = mockMvc.perform(get("/rest/tasks/{id}", taskToUpdateId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        TaskController.TaskDto updatedTask = objectMapper.readValue(response, TaskController.TaskDto.class);

        assertEquals(taskToUpdateId, updatedTask.id());
        assertEquals("updated task", updatedTask.name());
        assertEquals("new task name", updatedTask.description());
    }
}