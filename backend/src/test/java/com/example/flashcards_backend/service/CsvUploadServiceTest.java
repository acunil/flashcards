package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CsvUploadServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CsvUploadServiceImpl service;

    private LogCaptor logCaptor;

    @Captor
    private ArgumentCaptor<List<Card>> cardListCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(CsvUploadServiceImpl.class);
        logCaptor.clearLogs();
    }

    @Test
    void uploadCsv_filtersInvalidPartitionsDuplicatesAndSavesValid() throws Exception {
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

        Card savedCard = Card.builder().front("f3").back("b3").id(1L).build();
        when(cardRepository.saveAll(cardListCaptor.capture())).thenReturn(List.of(savedCard));

        CsvUploadResponseDto csvUploadResponseDTO = service.uploadCsv(is);

        assertThat(logCaptor.getWarnLogs()).hasSize(2)
            .allSatisfy(msg -> assertThat(msg).startsWith("Skipping invalid row:"));

        assertThat(logCaptor.getInfoLogs())
            .hasSize(3)
            .containsSequence("Duplicate, skipping: front='f4', back='b4'",
                "Found 1 duplicates",
                "Saved 1 new cards");

        List<Card> toSaveCaptured = cardListCaptor.getValue();
        assertThat(toSaveCaptured).containsExactly(Card.builder().front("f3").back("b3").build());

        assertThat(csvUploadResponseDTO.saved()).containsExactly(CardResponse.fromEntity(savedCard));
        Card expectedDuplicate = Card.builder().front("f4").back("b4").build();
        assertThat(csvUploadResponseDTO.duplicates()).containsExactly(CardResponse.fromEntity(expectedDuplicate));
    }

    @Test
    void uploadCsv_ioExceptionIsLoggedAndRethrown() throws Exception {
        try (InputStream badStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("fail");
            }
        }) {
            assertThatThrownBy(() -> service.uploadCsv(badStream))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("fail");
        }

        assertThat(logCaptor.getErrorLogs())
            .singleElement()
            .isEqualTo("CSV processing error");
    }
}
