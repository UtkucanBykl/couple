package com.example.couple.service;

import com.example.couple.entity.User;
import com.example.couple.exception.BadRequestException;
import com.example.couple.observability.CoupleMetrics;
import com.example.couple.repository.CoupleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CoupleValidateService {

    private final CoupleRepository coupleRepository;
    private final CoupleMetrics coupleMetrics;

    public void validate(User firstUser, User secondUser) {
        boolean exists = coupleRepository.existsRelation(firstUser, secondUser);
        if (exists) {
            coupleMetrics.invitationAcceptFailed();
            throw new BadRequestException("Zaten arkadaşlık var");
        }
    }
}