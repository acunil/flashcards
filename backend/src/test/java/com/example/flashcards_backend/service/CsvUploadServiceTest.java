package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CsvUploadServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private CsvUploadServiceImpl service;

    private LogCaptor logCaptor;

    @Captor
    private ArgumentCaptor<List<Card>> cardListCaptor;

    private Subject subject;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(CsvUploadServiceImpl.class);
        logCaptor.clearLogs();
        subject = Subject.builder().id(1L).name("Subject 1").build();
    }

    @Test
    void uploadCsv_filtersInvalidPartitionsDuplicatesAndSavesValid() throws Exception {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        String csv = """
            front,back
            ,b1
            f2,
            f3,b3
            f4,b4
            """;
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(cardRepository.existsByFrontAndBack("f3", "b3")).thenReturn(false);
        when(cardRepository.existsByFrontAndBack("f4", "b4")).thenReturn(true);

        Card savedCard = Card.builder().front("f3").back("b3").id(1L).subject(subject).build();
        when(cardRepository.saveAll(cardListCaptor.capture())).thenReturn(List.of(savedCard));

        CsvUploadResponseDto csvUploadResponseDTO = service.uploadCsv(is, 1L);

        assertThat(logCaptor.getWarnLogs()).hasSize(2)
            .allSatisfy(msg -> assertThat(msg).startsWith("Skipping invalid row:"));

        assertThat(logCaptor.getInfoLogs())
            .hasSize(3)
            .containsSequence("Duplicate, skipping: front='f4', back='b4'",
                "Found 1 duplicates",
                "Saved 1 new cards");

        List<Card> toSaveCaptured = cardListCaptor.getValue();
        assertThat(toSaveCaptured).hasSize(1);
        assertThat(toSaveCaptured.getFirst().getFront()).isEqualTo("f3");
        assertThat(toSaveCaptured.getFirst().getBack()).isEqualTo("b3");
        verify(cardRepository).existsByFrontAndBack("f3", "b3");

        assertThat(csvUploadResponseDTO.saved()).containsExactly(CardResponse.fromEntity(savedCard));
        Card expectedDuplicate = Card.builder().front("f4").back("b4").subject(subject).build();
        assertThat(csvUploadResponseDTO.duplicates()).containsExactly(CardResponse.fromEntity(expectedDuplicate));
    }

    @Test
    void uploadCsv_ioExceptionIsLoggedAndRethrown() throws Exception {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
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
