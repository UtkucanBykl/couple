package com.example.couple.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class DayWriteRequest {
    @NotNull
    @Size(min = 3)
    private String title;

    @NotNull
    private String description;

    private MultipartFile coverPhoto;

    @NotNull
    private LocalDate date;

    @NotNull
    private String name;
}
