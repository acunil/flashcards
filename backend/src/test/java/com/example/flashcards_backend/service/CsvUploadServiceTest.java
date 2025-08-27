package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.*;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CsvUploadServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CsvUploadServiceImpl service;

    private LogCaptor logCaptor;

    private Subject subject;
    private User user;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(CsvUploadServiceImpl.class);
        logCaptor.clearLogs();
        user = User.builder().id(UUID.randomUUID()).build();
        subject = Subject.builder().id(1L).name("Subject 1").user(user).build();
    }

    @Test
    void uploadCsv_filtersInvalidPartitionsDuplicatesAndSavesValid() throws Exception {
        when(subjectRepository.findByIdWithUserAndSubjects(1L)).thenReturn(Optional.of(subject));

        String csv = """
                front,back,hint_front,hint_back,decks
                ,b1,,,
                f2,,,,
                f3,b3,hf1,hb1,d1;d2
                f4,b4,,hb2,
                """;
        InputStream csvInputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        // Duplicate detection
        when(cardRepository.existsByFrontAndBackAndSubjectId("f3", "b3", 1L)).thenReturn(false);
        when(cardRepository.existsByFrontAndBackAndSubjectId("f4", "b4", 1L)).thenReturn(true);

        // Decks resolved up‑front
        Deck deck1 = Deck.builder().id(1L).name("d1").subject(subject).user(user).build();
        Deck deck2 = Deck.builder().id(2L).name("d2").subject(subject).user(user).build();

        // Card that will be “saved” by cardService
        Card savedCard = Card.builder()
                .id(42L)
                .front("f3")
                .back("b3")
                .hintFront("hf1")
                .hintBack("hb1")
                .subject(subject)
                .user(user)
                .build();
        savedCard.addDecks(Set.of(deck1, deck2));

        when(cardService.createCards(anyList()))
                .thenReturn(List.of(CreateCardResponse.builder()
                        .id(savedCard.getId())
                        .front(savedCard.getFront())
                        .back(savedCard.getBack())
                        .hintFront(savedCard.getHintFront())
                        .hintBack(savedCard.getHintBack())
                        .decks(savedCard.getDecks().stream().map(DeckSummary::fromEntity).toList())
                        .alreadyExisted(false)
                        .build()));

        CsvUploadResponseDto result = service.uploadCsv(csvInputStream, subject.getId());

        // Warnings for invalid rows
        assertThat(logCaptor.getWarnLogs()).hasSize(2)
                .allSatisfy(msg -> assertThat(msg).startsWith("Skipping invalid row:"));

        // Result DTO matches our mock
        List<CardResponse> saved = result.saved();
        assertThat(saved).singleElement().satisfies(card -> {
            assertThat(card.front()).isEqualTo("f3");
            assertThat(card.back()).isEqualTo("b3");
            assertThat(card.hintFront()).isEqualTo("hf1");
            assertThat(card.hintBack()).isEqualTo("hb1");
            assertThat(card.decks()).hasSize(2)
                    .containsExactlyInAnyOrder(DeckSummary.fromEntity(deck1), DeckSummary.fromEntity(deck2));
        });
        assertThat(result.duplicates())
                .containsExactly(CardResponse.builder().front("f4").back("b4").build());

        // CardService invoked once with the correct request list
        verify(cardService).createCards(argThat(reqs ->
                reqs.size() == 1 &&
                        reqs.getFirst().deckNames().containsAll(Set.of("d1", "d2"))
        ));
    }

    @Test
    void uploadCsv_ioExceptionIsLoggedAndRethrown() throws Exception {
        when(subjectRepository.findByIdWithUserAndSubjects(1L)).thenReturn(Optional.of(subject));
        try (InputStream badStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("fail");
            }
        }) {
            assertThatThrownBy(() -> service.uploadCsv(badStream, 1L))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("fail");
        }

        assertThat(logCaptor.getErrorLogs())
                .singleElement()
                .isEqualTo("CSV processing error");
    }
}
