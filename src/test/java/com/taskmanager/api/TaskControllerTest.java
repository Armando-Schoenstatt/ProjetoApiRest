package com.taskmanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.api.dto.AuthDTO;
import com.taskmanager.api.dto.TaskDTO;
import com.taskmanager.api.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        var reg = new AuthDTO.RegisterRequest("task@test.com", "Task User", "pass1234");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reg)))
                .andReturn();

        // Se já existe, faz login
        if (result.getResponse().getStatus() != 201) {
            result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(new AuthDTO.LoginRequest("task@test.com", "pass1234"))))
                    .andReturn();
        }

        token = mapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void shouldCreateTask() throws Exception {
        var request = new TaskDTO.Request("Minha Tarefa", "Descrição", Task.Status.PENDING, Task.Priority.HIGH, null);
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Minha Tarefa"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldListTasks() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        var create = new TaskDTO.Request("Para Atualizar", null, null, null, null);
        MvcResult created = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andReturn();

        Long id = mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        var update = new TaskDTO.Request("Atualizada", "Nova desc", Task.Status.DONE, Task.Priority.LOW, null);
        mockMvc.perform(put("/api/tasks/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Atualizada"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        var create = new TaskDTO.Request("Para Deletar", null, null, null, null);
        MvcResult created = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andReturn();

        Long id = mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/tasks/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFilterByStatus() throws Exception {
        mockMvc.perform(get("/api/tasks?status=PENDING")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
