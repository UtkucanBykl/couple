package com.example.couple.service;

import com.example.couple.config.RabbitConfig;
import com.example.couple.dto.event.CoupleInviteEvent;
import com.example.couple.dto.request.CoupleInviteRequest;
import com.example.couple.dto.request.CoupleInviteUpdateRequest;
import com.example.couple.dto.response.CoupleInviteResponse;
import com.example.couple.entity.CoupleInvite;
import com.example.couple.entity.User;
import com.example.couple.enums.InviteStatus;
import com.example.couple.enums.OutboxAggregateType;
import com.example.couple.enums.OutboxEventType;
import com.example.couple.exception.BadRequestException;
import com.example.couple.exception.NotFoundException;
import com.example.couple.mapper.CoupleInviteMapper;
import com.example.couple.repository.CoupleInviteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class CoupleInviteService {

  private final CoupleInviteRepository coupleInviteRepository;
  private final UserService userService;
  private final OutboxService outboxService;
  private final CoupleInviteMapper coupleInviteMapper;

  @Transactional
  public CoupleInviteResponse create(CoupleInviteRequest request, Long userId) {
    User senderUser =
        userService.findById(userId).orElseThrow(() -> new NotFoundException("Kullanıcı yok"));

    User receiverUser =
        userService
            .findById(request.getReceiverUserId())
            .orElseThrow(() -> new NotFoundException("Kullanıcı yok"));

    if (coupleInviteRepository.existsInviteBetweenUsers(
        senderUser.getId(), receiverUser.getId(), InviteStatus.PENDING)) {
      throw new BadRequestException("Bu kullanıcılar arasında bekleyen davet var");
    }

    CoupleInvite coupleInvite = CoupleInvite.createPending(senderUser, receiverUser);
    CoupleInvite savedInvite = coupleInviteRepository.save(coupleInvite);

    outboxService.save(
        OutboxEventType.COUPLE_INVITE,
        OutboxAggregateType.COUPLE,
        savedInvite.getId(),
        new CoupleInviteEvent(
            savedInvite.getId(),
            savedInvite.getSenderUser().getId(),
            savedInvite.getReceiverUser().getId(),
            Instant.now()),
        RabbitConfig.COUPLE_EXCHANGE,
        RabbitConfig.COUPLE_INVITE_CREATED_ROUTING_KEY);
    return coupleInviteMapper.toResponse(savedInvite);
  }

  @Transactional
  public CoupleInviteResponse updateStatus(
      CoupleInviteUpdateRequest request, Long userId, Long coupleInviteId) {
    CoupleInvite coupleInvite =
        coupleInviteRepository
            .findWithUsersById(coupleInviteId)
            .orElseThrow(() -> new NotFoundException("Davet yok"));

    if (!coupleInvite.getReceiverUser().getId().equals(userId)) {
      throw new NotFoundException("Davet yok");
    }

    if (request.getStatus() == InviteStatus.ACCEPTED) {
      coupleInvite.accept();
    } else if (request.getStatus() == InviteStatus.REJECTED) {
      coupleInvite.reject();
    } else {
      throw new BadRequestException("Geçersiz davet durumu");
    }

    return coupleInviteMapper.toResponse(coupleInviteRepository.save(coupleInvite));
  }
}
