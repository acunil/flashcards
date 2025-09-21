package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.SubjectRequest;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Mock
    private SubjectRepository subjectRepository;

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
        when(subjectRepository.findAll()).thenReturn(List.of(subject1, subject2));

        List<Subject> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Subject 1");
        assertThat(result.get(1).getName()).isEqualTo("Subject 2");
    }

    @Test
    void findByUserId() {
        when(subjectRepository.findByUserId(USER_ID)).thenReturn(List.of(subject1, subject2));

        List<Subject> result = service.findByUserId(USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Subject 1");
        assertThat(result.get(1).getName()).isEqualTo("Subject 2");
    }

    @Test
    void findByIdFound() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject1));

        Subject result = service.findById(1L);

        assertThat(result.getName()).isEqualTo("Subject 1");
    }

    @Test
    void findByIdNotFound() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(SubjectNotFoundException.class);
    }

    @Test
    void create() {
        SubjectRequest request = SubjectRequest.builder()
                .name("New Subject")
                .build();
        Subject entity = request.toEntity();
        when(subjectRepository.save(any(Subject.class))).thenReturn(entity);

    Subject result = service.create(request, new User());

        assertThat(result.getName()).isEqualTo("New Subject");

        ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(captor.capture());
    }

    @Test
    void updateFound() {
        SubjectRequest updateRequest = SubjectRequest.builder()
                .name("Updated Subject")
                .frontLabel("Front")
                .backLabel("Back")
                .defaultSide(Subject.Side.FRONT)
                .displayDeckNames(true)
                .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject1));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subject result = service.update(1L, updateRequest);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Subject");
        assertThat(result.getFrontLabel()).isEqualTo("Front");
        assertThat(result.getBackLabel()).isEqualTo("Back");
        assertThat(result.getDefaultSide()).isEqualTo(Subject.Side.FRONT);
        assertThat(result.getDisplayDeckNames()).isTrue();
        verify(subjectRepository).save(subject1);
    }

    @Test
    void updateNotFound() {
        SubjectRequest updateRequest = SubjectRequest.builder()
                .name("Updated Subject")
                .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, updateRequest))
                .isInstanceOf(SubjectNotFoundException.class);
    }

    @Test
    void delete() {
        service.delete(1L);

        verify(subjectRepository).deleteById(1L);
    }
}