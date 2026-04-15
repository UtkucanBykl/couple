package com.example.couple.service;


import com.example.couple.dto.request.DayWriteRequest;
import com.example.couple.dto.response.DayWriteResponse;
import com.example.couple.entity.Couple;
import com.example.couple.entity.Day;
import com.example.couple.entity.User;
import com.example.couple.exception.BadRequestException;
import com.example.couple.mapper.DayMapper;
import com.example.couple.repository.CoupleRepository;
import com.example.couple.repository.DayRepository;
import com.example.couple.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DayService {
    private final DayRepository dayRepository;
    private final DayMapper dayMapper;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    @Transactional
    public DayWriteResponse createDay(DayWriteRequest dayWriteRequest, Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Kullanıcı bulunamadı")
        );
        if(!user.hasActiveCouple()){
            throw new BadRequestException("Kullanıcının couple yok");
        }
        Couple couple = user.getActiveCouple();
        if(dayRepository.existsDayByCoupleAndDate(couple, dayWriteRequest.getDate())){
            throw new BadRequestException("Bugün için Day var");
        }
        String folderName = String.format("couple-%d", couple.getId());
        String path = fileStorageService.saveImage(dayWriteRequest.getCoverPhoto(), folderName);

        Day day = Day.create(
                couple,
                dayWriteRequest.getName(),
                dayWriteRequest.getDescription(),
                dayWriteRequest.getDate(),
                path
        );
        Day savedDay = dayRepository.save(day);

        return dayMapper.toResponse(savedDay);
    }
}
