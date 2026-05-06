package com.example.couple.contoller;

import com.example.couple.config.StaticResourceConfig;
import com.example.couple.controller.CoupleController;
import com.example.couple.dto.request.CoupleWriteRequest;

import com.example.couple.dto.response.CoupleWriteResponse;
import com.example.couple.dto.response.UserBasicResponse;
import com.example.couple.security.CustomUserPrincipal;
import com.example.couple.security.JwtService;
import com.example.couple.service.CoupleService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(
    controllers = CoupleController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = StaticResourceConfig.class))
@AutoConfigureMockMvc()
class CoupleControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CoupleService coupleService;

  @MockitoBean private JwtService jwtService;

  @MockitoBean private UserDetailsService userDetailsService;

  @Test
  void shouldCreateCouple() throws Exception {
    CoupleWriteRequest coupleWriteRequest = new CoupleWriteRequest();
    coupleWriteRequest.setSecondUserID(2L);
    UserBasicResponse userBasicResponse = new UserBasicResponse(2L, "user2", "test2@test.com");
    CoupleWriteResponse response = new CoupleWriteResponse(2L, userBasicResponse);
    given(coupleService.createCouple(any(CoupleWriteRequest.class), any(Long.class)))
        .willReturn(response);

    CustomUserPrincipal principal = new CustomUserPrincipal(1L, "user1", "123");

    mockMvc
        .perform(
            post("/api/v1/couples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coupleWriteRequest))
                .with(user(principal))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2));
  }
}
