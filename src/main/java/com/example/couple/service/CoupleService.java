package com.example.couple.service;

import com.example.couple.config.RabbitConfig;
import com.example.couple.dto.event.CoupleCreatedNotificationEvent;
import com.example.couple.dto.request.CoupleWriteRequest;
import com.example.couple.dto.response.CoupleDetailResponse;
import com.example.couple.dto.response.CoupleWriteResponse;
import com.example.couple.entity.Couple;
import com.example.couple.entity.OutboxEvent;
import com.example.couple.entity.User;
import com.example.couple.enums.OutboxAggregateType;
import com.example.couple.enums.OutboxEventType;
import com.example.couple.enums.OutboxStatus;
import com.example.couple.exception.BadRequestException;
import com.example.couple.mapper.CoupleMapper;
import com.example.couple.repository.CoupleRepository;
import com.example.couple.repository.OutboxRepository;
import com.example.couple.repository.UserRepository;
import com.example.couple.security.CustomUserPrincipal;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@RequiredArgsConstructor
public class CoupleService {
  private final CoupleRepository coupleRepository;
  private final CoupleValidateService coupleValidateService;
  private final CoupleMapper coupleMapper;
  private final UserRepository userRepository;
  private final OutboxService outboxService;

  @Transactional
  public CoupleWriteResponse createCouple(CoupleWriteRequest coupleWriteRequest, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BadRequestException("Kullanıcı bulunamadı"));
    User secondUser =
        userRepository
            .findById(coupleWriteRequest.getSecondUserID())
            .orElseThrow(() -> new BadRequestException("Kullanıcı bulunamadı"));
    coupleValidateService.validate(user, secondUser);
    Couple couple = Couple.createActive(user, secondUser);
    Couple savedCouple = coupleRepository.save(couple);
    savedCouple.attachActiveCoupleToUsers();

    outboxService.save(
        OutboxEventType.COUPLE_CREATE,
        OutboxAggregateType.COUPLE,
        savedCouple.getId(),
        new CoupleCreatedNotificationEvent(savedCouple.getId(), secondUser.getId()),
        RabbitConfig.COUPLE_EXCHANGE,
        RabbitConfig.COUPLE_CREATED_ROUTING_KEY);

    return coupleMapper.toCreateResponse(savedCouple);
  }

  @Transactional
  public void deleteCouple(Long id, Long userId) {
    Couple couple =
        coupleRepository
            .findCouple(id, userId)
            .orElseThrow(() -> new BadRequestException("Böyle bir couple yok"));
    couple.detachFromUsers();
    coupleRepository.delete(couple);

  }

  @Transactional(readOnly = true)
  public CoupleDetailResponse detailCouple(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BadRequestException("Kullanıcı bulunamadı"));
    Couple couple =
        coupleRepository
            .findCoupleByUserWithFetched(user.getId())
            .orElseThrow(() -> new BadRequestException("Henüz aktif bir couple yok"));
    return coupleMapper.toDetailResponse(couple, user);
  }
}
