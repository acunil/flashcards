package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.SubjectDto;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;

    public List<Subject> findAll() {
        return repository.findAll();
    }

    public List<Subject> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public Subject findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));
    }

    @Transactional
    public Subject create(SubjectDto subjectDto) {
        return repository.save(subjectDto.toEntity());
    }

    @Transactional
    public Subject update(Long id, SubjectDto updated) {
        return repository.findById(id)
                .map(subject -> {
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
