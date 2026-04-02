package com.example.couple.repository;

import com.example.couple.entity.Couple;
import com.example.couple.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, Long> {
    @Query("SELECT COUNT(a)>0 FROM Couple a where " +
            "(a.firstUser = :u1 AND a.secondUser = :u2) OR" +
            "(a.firstUser = :u2 AND a.secondUser = :u1)"
    )
    boolean existsRelation(@Param("u1") User u1, @Param("u2") User u2);

    @Query(
            "SELECT a FROM Couple a where " +
                    "(a.firstUser.id = :user1Id AND a.secondUser.id = :user2Id) OR" +
                    "(a.firstUser.id = :user2Id AND a.secondUser.id = :user1Id)"
    )
    Optional<Couple> findCouple(@Param("user1Id") Long user1Id,@Param("user2Id") Long user2Id);

    @Query(
            "SELECT COUNT(a) > 1 FROM Couple a where " +
                    "(a.firstUser.id = :userId and a.status = CoupleStatus.ACTIVE) OR"+
                    "(a.secondUser.id = :userId and a.status = CoupleStatus.ACTIVE)"
    )
    boolean existsByUserActiveCouple(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"firstUser", "secondUser"})
    @Query(
            "SELECT a FROM Couple a " +
                    "WHERE a.firstUser.id = :userId or a.secondUser.id = :userId"
    )
    Optional<Couple> findCoupleByUserWithFetched(@Param("userId") Long userId);
}
