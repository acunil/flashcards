package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.SubjectDto;
import com.example.flashcards_backend.dto.SubjectRequest;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

  private final SubjectRepository repository;

  public List<SubjectDto> findForUser(User user) {
    log.info("Finding subjects for user {}", user.getId());
    List<Subject> subjects = repository.findByUserId(user.getId());
    log.info("Found {} subjects for user {}", subjects.size(), user.getId());
    return subjects.stream().map(SubjectDto::fromEntity).toList();
  }

  public SubjectDto findDtoById(Long id) {
    return SubjectDto.fromEntity(findById(id));
  }

  @Transactional
  public Subject create(SubjectRequest request, User user) {
    log.info("Creating subject for user {}", user.getId());
    Subject subject = request.toEntity();
    subject.setUser(user);
    return repository.save(subject);
  }

  @Transactional
  public SubjectDto update(Long id, SubjectRequest updated) {
    Subject subject = findById(id);
    log.info("Updating subject {} for user {}", subject.getName(), subject.getUser().getUsername());
    subject.setName(updated.name());
    subject.setFrontLabel(updated.frontLabel());
    subject.setBackLabel(updated.backLabel());
    subject.setDefaultSide(updated.defaultSide());
    subject.setDisplayDeckNames(updated.displayDeckNames());
    subject.setCardOrder(updated.cardOrder());
    return SubjectDto.fromEntity(subject);
  }

  @Transactional
  public void delete(Long id) {
    repository.deleteById(id);
  }

  protected Subject findById(Long id) {
    log.info("Finding subject with id {}", id);
    return repository.findById(id).orElseThrow(() -> new SubjectNotFoundException(id));
  }
}
