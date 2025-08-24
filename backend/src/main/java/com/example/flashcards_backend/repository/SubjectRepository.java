package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByUserId(UUID userId);

    @Query("select s from Subject s join fetch s.user u left join fetch u.subjects where s.id = :id")
    Optional<Subject> findByIdWithUserAndSubjects(@Param("id") Long id);

    Optional<Subject> findByName(String name);
}
