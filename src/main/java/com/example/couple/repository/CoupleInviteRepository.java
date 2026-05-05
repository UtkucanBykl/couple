package com.example.couple.repository;

import com.example.couple.entity.CoupleInvite;
import com.example.couple.entity.User;
import com.example.couple.enums.InviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CoupleInviteRepository extends JpaRepository<CoupleInvite, Long> {
    @EntityGraph(attributePaths = {"senderUser", "receiverUser"})
    Optional<CoupleInvite> findWithUsersById(@NonNull Long id);

    @EntityGraph(attributePaths = {"senderUser"})
    Page<CoupleInvite> findByReceiverUserId(Long receiverUserId, Pageable pageable);

    @EntityGraph(attributePaths = {"receiverUser"})
    Page<CoupleInvite> findBySenderUserId(Long senderUserId, Pageable pageable);

    @Query("""
            select count(ci) > 0
            from CoupleInvite ci
            where
                ci.status = :status and (
                (ci.senderUser.id = :user1 and ci.receiverUser.id = :user2)
                or
                (ci.senderUser.id = :user2 and ci.receiverUser.id = :user1)
                    )
            
            """)
    boolean existsInviteBetweenUsers(
            Long user1,
            Long user2,
            InviteStatus status
    );
}
