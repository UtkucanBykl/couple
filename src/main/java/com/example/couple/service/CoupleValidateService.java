package com.example.couple.service;

import com.example.couple.entity.User;
import com.example.couple.exception.BadRequestException;
import com.example.couple.repository.CoupleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CoupleValidateService{
    private final CoupleRepository coupleRepository;

    public void validate(User firstUser, User secondUser){
        boolean hasFirstUserCouple = coupleRepository.existsByUserActiveCouple(firstUser.getId());
        if(hasFirstUserCouple){
            throw new BadRequestException("Aktif bir couple olabilir");
        }
        boolean hasSecondUserCouple = coupleRepository.existsByUserActiveCouple(secondUser.getId());
        if(hasSecondUserCouple){
            throw new BadRequestException("Aktif bir couple olabilir");
        }
        boolean isExists = coupleRepository.existsRelation(firstUser, secondUser);
        if(isExists){
            throw new BadRequestException("Zaten arkadaşlık var");
        }
    }
}