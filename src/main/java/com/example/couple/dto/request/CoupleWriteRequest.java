package com.example.couple.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoupleWriteRequest {

    @NotNull
    private Long secondUserID;
}
