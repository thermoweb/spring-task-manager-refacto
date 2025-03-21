package org.thermoweb.tasks.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.thermoweb.tasks.model.TaskStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = BEFORE_TEST_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_user() throws Exception {
        UserController.UserCreationRequest userCreationRequest = new UserController.UserCreationRequest("Deuxfleurs", "deuxfleurs@thermoweb.org");
        mockMvc.perform(post("/rest/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(userCreationRequest.name()))
                .andExpect(jsonPath("$.email").value(userCreationRequest.email()));
    }
    @Test
    void should_not_create_user_when_email_is_invalid() throws Exception {
        UserController.UserCreationRequest userCreationRequest = new UserController.UserCreationRequest("Ghengis", "genghisthermoweb.org");
        mockMvc.perform(post("/rest/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_not_create_user_when_already_exists() throws Exception {
        UserController.UserCreationRequest userCreationRequest = new UserController.UserCreationRequest("Jedusor", "tom.jedusor@thermoweb.org");
        mockMvc.perform(post("/rest/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_create_task_for_user() throws Exception {
        Long alreadyExistingUserId = 1L;
        TaskController.TaskCreationRequest taskCreationRequest = new TaskController.TaskCreationRequest("mytask", "my task", "my task description");

        String response = mockMvc.perform(post("/rest/users/{userId}:create-task", alreadyExistingUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCreationRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        TaskController.TaskDto createdTask = objectMapper.readValue(response, TaskController.TaskDto.class);
        assertEquals(taskCreationRequest.id(), createdTask.id());
        assertEquals(taskCreationRequest.name(), createdTask.name());
        assertEquals(taskCreationRequest.description(), createdTask.description());
        assertEquals(alreadyExistingUserId,createdTask.assignee().id());
        assertEquals(TaskStatus.OPEN, createdTask.taskStatus());
    }

}