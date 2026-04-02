package com.example.couple.dto.request;


import com.example.couple.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoupleWriteRequest {

    @NotNull
    private Long secondUserID;
}
