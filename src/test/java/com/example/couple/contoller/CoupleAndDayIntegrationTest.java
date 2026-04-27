package com.example.couple.contoller;

import com.example.couple.dto.request.CoupleWriteRequest;
import com.example.couple.entity.User;
import com.example.couple.repository.UserRepository;
import com.example.couple.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CoupleAndDayIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldCreateDay() throws Exception {
    User user1 = User.create(
            "user1",
            "user1@test.com",
            "hashed_password",
            "12345"
    );
    userRepository.save(user1);

    User user2 = User.create(
            "user2",
            "user2@test.com",
            "hashed_password",
            "12344"
    );
    userRepository.save(user2);

    String accessToken = jwtService.generateToken(user1.getUsername());

    CoupleWriteRequest coupleWriteRequest = new CoupleWriteRequest();
    coupleWriteRequest.setSecondUserID(user2.getId());

    mockMvc
        .perform(
            post("/api/v1/couples")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coupleWriteRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists());

      MockMultipartFile dummyImage = new MockMultipartFile(
              "coverPhoto",
              "ani.jpg",
              MediaType.IMAGE_JPEG_VALUE,
              "test".getBytes()
      );
    LocalDate today = LocalDate.now();

    mockMvc.perform(multipart("/api/v1/days")
                    .file(dummyImage)
                    .param("title", "My day title")
                    .param("description", "Some description")
                    .param("date", today.toString())
                    .param("name", "Utku")
            .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isCreated());
  }
}
