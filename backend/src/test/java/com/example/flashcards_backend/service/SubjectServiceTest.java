package com.example.flashcards_backend.service;
import com.example.flashcards_backend.dto.SubjectDto;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository repository;

    @InjectMocks
    private SubjectService service;

    private Subject subject1;
    private Subject subject2;

    @BeforeEach
    void setUp() {
        subject1 = Subject.builder().id(1L).name("Subject 1").build();
        subject2 = Subject.builder().id(2L).name("Subject 2").build();
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(List.of(subject1, subject2));

        List<Subject> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Subject 1");
        assertThat(result.get(1).getName()).isEqualTo("Subject 2");
    }

    @Test
    void findByIdFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(subject1));

        Optional<Subject> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Subject 1");
    }

    @Test
    void findByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<Subject> result = service.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void create() {
        SubjectDto dto = new SubjectDto(null, "New Subject", null, null, null, null);
        Subject entity = dto.toEntity();
        when(repository.save(any(Subject.class))).thenReturn(entity);

        Subject result = service.create(dto);

        assertThat(result.getName()).isEqualTo("New Subject");

        ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
        verify(repository).save(captor.capture());
    }

    @Test
    void updateFound() {
        SubjectDto updatedDto = new SubjectDto(1L, "Updated Subject", "Front", "Back", Subject.Side.FRONT, true);
        when(repository.findById(1L)).thenReturn(Optional.of(subject1));
        when(repository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subject result = service.update(1L, updatedDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Subject");
        assertThat(result.getFrontLabel()).isEqualTo("Front");
        assertThat(result.getBackLabel()).isEqualTo("Back");
        assertThat(result.getDefaultSide()).isEqualTo(Subject.Side.FRONT);
        assertThat(result.getDisplayDeckNames()).isTrue();
        verify(repository).save(subject1);
    }

    @Test
    void updateNotFound() {
        SubjectDto updatedDto = new SubjectDto(1L, "Updated Subject", null, null, null, null);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, updatedDto))
                .isInstanceOf(SubjectNotFoundException.class);
    }

    @Test
    void delete() {
        service.delete(1L);

        verify(repository).deleteById(1L);
    }
}