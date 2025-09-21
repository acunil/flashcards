package com.example.flashcards_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.flashcards_backend.dto.SubjectDto;
import com.example.flashcards_backend.dto.SubjectRequest;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.SubjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

  static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

  @Mock private SubjectRepository subjectRepository;

  @InjectMocks private SubjectService service;

  private Subject subject1;
  private Subject subject2;

  @BeforeEach
  void setUp() {
    User testUser = User.builder().id(USER_ID).username("testuser").build();
    subject1 = Subject.builder().id(1L).name("Subject 1").user(testUser).build();
    subject2 = Subject.builder().id(2L).name("Subject 2").user(testUser).build();
  }

  @Test
  void findForUser() {
    when(subjectRepository.findByUserId(USER_ID)).thenReturn(List.of(subject1, subject2));

    User user = new User();
    user.setId(USER_ID);
    List<SubjectDto> result = service.findForUser(user);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).name()).isEqualTo("Subject 1");
    assertThat(result.get(1).name()).isEqualTo("Subject 2");
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
    SubjectRequest request = SubjectRequest.builder().name("New Subject").build();
    Subject entity = request.toEntity();
    when(subjectRepository.save(any(Subject.class))).thenReturn(entity);

    Subject result = service.create(request, new User());

    assertThat(result.getName()).isEqualTo("New Subject");

    ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
    verify(subjectRepository).save(captor.capture());
  }

  @Test
  void updateFound() {
    SubjectRequest updateRequest =
        SubjectRequest.builder()
            .name("Updated Subject")
            .frontLabel("Front")
            .backLabel("Back")
            .defaultSide(Subject.Side.FRONT)
            .displayDeckNames(true)
            .cardOrder(Subject.CardOrder.OLDEST)
            .build();
    when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject1));

    SubjectDto result = service.update(1L, updateRequest);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.name()).isEqualTo("Updated Subject");
    assertThat(result.frontLabel()).isEqualTo("Front");
    assertThat(result.backLabel()).isEqualTo("Back");
    assertThat(result.defaultSide()).isEqualTo(Subject.Side.FRONT);
    assertThat(result.displayDeckNames()).isTrue();
    assertThat(result.cardOrder()).isEqualTo(Subject.CardOrder.OLDEST);
  }

  @Test
  void updateNotFound() {
    SubjectRequest updateRequest = SubjectRequest.builder().name("Updated Subject").build();
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
