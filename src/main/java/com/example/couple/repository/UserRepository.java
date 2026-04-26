package com.example.couple.repository;

import com.example.couple.dto.response.UserSearchResponse;
import com.example.couple.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByFriendCode(String friendCode);

  @Query("""
    SELECT new com.example.couple.dto.response.UserSearchResponse(
        u.username,
        u.email,
        u.friendCode
    )
    FROM User u
    WHERE u.id != :currentUserId
      AND (:friendCode IS NULL OR u.friendCode = :friendCode)
      AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))
      AND (:email IS NULL OR LOWER(u.email) = LOWER(:email))
""")
  Page<UserSearchResponse> searchUsers(
          @Param("currentUserId") Long currentUserId,
          @Param("friendCode") String friendCode,
          @Param("username") String username,
          @Param("email") String email,
          Pageable pageable
  );
}
