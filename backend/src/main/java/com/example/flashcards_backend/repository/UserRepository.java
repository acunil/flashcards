package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.dto.UserDto;
import com.example.flashcards_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(UUID userId);

    @Query("""
            SELECT new com.example.flashcards_backend.dto.UserDto(
                u.username, u.id, u.isActive
            )
            FROM User u
            """)
    List<UserDto> findAllUsersAsDtos();
}
