package com.example.couple.contoller;

import com.example.couple.controller.UserController;
import com.example.couple.dto.request.UserCreateRequest;
import com.example.couple.dto.response.UserCreateResponse;
import com.example.couple.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("utku1997");
        request.setFirstName("Utku");
        request.setLastName("Biyikli");
        request.setEmail("utku@example.com");
        request.setPassword("1234567890");

        UserCreateResponse response =
                new UserCreateResponse(1L, "utku1997", "utku@example.com", "1234567890L", "e1", "e2");

        given(userService.createUser(org.mockito.ArgumentMatchers.any(UserCreateRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("utku1997"))
                .andExpect(jsonPath("$.email").value("utku@example.com"))
                .andExpect(jsonPath("$.friendCode").value("1234567890L"))
                .andExpect(jsonPath("$.accessToken").value("e1"))
                .andExpect(jsonPath("$.refreshToken").value("e2"));

    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("");
        request.setEmail("not-an-email");
        request.setPassword("123");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}