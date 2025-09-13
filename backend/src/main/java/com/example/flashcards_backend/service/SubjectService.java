package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.SubjectRequest;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.exception.UserNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.example.flashcards_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

  private final SubjectRepository repository;
  private final UserRepository userRepository;

  public List<Subject> findAll() {
    return repository.findAll();
  }

  public List<Subject> findByUserId(UUID userId) {
    log.info("Finding subjects for user {}", userId);
    List<Subject> subjects = repository.findByUserId(userId);
    log.info("Found {} subjects for user {}", subjects.size(), userId);
    return subjects;
  }

  public Subject findById(Long id) {
    log.info("Finding subject with id {}", id);
    return repository.findById(id).orElseThrow(() -> new SubjectNotFoundException(id));
  }

  @Transactional
  public Subject create(SubjectRequest request, UUID userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    return create(request, user);
  }

  @Transactional
  public Subject create(SubjectRequest request, User user) {
    log.info("Creating subject for user {}", user.getId());
    Subject subject = request.toEntity();
    subject.setUser(user);
    return repository.save(subject);
  }

  @Transactional
  public Subject update(Long id, SubjectRequest updated) {
    return repository
        .findById(id)
        .map(
            subject -> {
              subject.setName(updated.name());
              subject.setFrontLabel(updated.frontLabel());
              subject.setBackLabel(updated.backLabel());
              subject.setDefaultSide(updated.defaultSide());
              subject.setDisplayDeckNames(updated.displayDeckNames());
              return repository.save(subject);
            })
        .orElseThrow(() -> new SubjectNotFoundException(id));
  }

  @Transactional
  public void delete(Long id) {
    repository.deleteById(id);
  }
}
