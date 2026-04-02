package com.example.couple.service;


import com.example.couple.dto.request.CoupleWriteRequest;
import com.example.couple.dto.response.CoupleDetailResponse;
import com.example.couple.dto.response.CoupleWriteResponse;
import com.example.couple.entity.Couple;
import com.example.couple.entity.User;
import com.example.couple.exception.BadRequestException;
import com.example.couple.mapper.CoupleMapper;
import com.example.couple.repository.CoupleRepository;
import com.example.couple.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class CoupleService {
    private final CoupleRepository coupleRepository;
    private final CoupleValidateService coupleValidateService;
    private final CoupleMapper coupleMapper;
    private final UserRepository userRepository;


    @Transactional
    public CoupleWriteResponse createCouple(CoupleWriteRequest coupleWriteRequest, User user){
        User secondUser = userRepository.findById(coupleWriteRequest.getSecondUserID()).orElseThrow(
                () -> new BadRequestException("Kullanıcı bulunamadı")
        );
        coupleValidateService.validate(user, secondUser);
        Couple couple = new Couple();
        couple.setFirstUser(user);
        couple.setSecondUser(secondUser);

        Couple savedCouple = coupleRepository.save(couple);
        user.setActiveCouple(couple);
        secondUser.setActiveCouple(couple);
        userRepository.save(user);
        userRepository.save(secondUser);
        return coupleMapper.toCreateResponse(savedCouple);
    }

    @Transactional
    public void deleteCouple(Long id, User user) throws RuntimeException{
        Couple couple = coupleRepository.findCouple(id, user.getId())
                .orElseThrow(() -> new BadRequestException("Böyle bir couple yok"));
        coupleRepository.delete(couple);
    }

    @Transactional(readOnly = true)
    public CoupleDetailResponse detailCouple(User user) throws BadRequestException {
        Couple couple = coupleRepository.findCoupleByUserWithFetched(user.getId())
                .orElseThrow(() -> new BadRequestException("Henüz aktif bir couple yok"));
        return coupleMapper.toDetailResponse(couple, user);
    }
}
