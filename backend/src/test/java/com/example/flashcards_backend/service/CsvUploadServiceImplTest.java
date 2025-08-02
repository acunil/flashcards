package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.UploadResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import nl.altindag.log.LogCaptor;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvUploadServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CsvUploadServiceImpl service;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(CsvUploadServiceImpl.class);
        logCaptor.clearLogs();
    }

    @Test
    void uploadCsv_filtersInvalidPartitionsDuplicatesAndSavesValid() throws Exception {
        String csv = "front,back\n" +
            ",b1\n" +
            "f2,\n" +
            "f3,b3\n" +
            "f4,b4\n";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        when(cardRepository.existsByFrontAndBack("f3", "b3")).thenReturn(false);
        when(cardRepository.existsByFrontAndBack("f4", "b4")).thenReturn(true);

        ArgumentCaptor<List<Card>> saveCaptor = ArgumentCaptor.forClass(List.class);
        Card savedCard = Card.builder().front("f3").back("b3").build();
        when(cardRepository.saveAll(saveCaptor.capture())).thenReturn(List.of(savedCard));

        UploadResponse uploadResponse = service.uploadCsv(is);

        List<String> warnLogs = logCaptor.getWarnLogs();
        assertThat(warnLogs).hasSize(2)
            .allSatisfy(msg -> assertThat(msg).startsWith("Skipping invalid row:"));

        List<String> infoLogs = logCaptor.getInfoLogs();
        assertThat(infoLogs)
            .singleElement()
            .isEqualTo("Duplicate, skipping: front='f4', back='b4'");

        List<Card> toSaveCaptured = saveCaptor.getValue();
        assertThat(toSaveCaptured).containsExactly(Card.builder().front("f3").back("b3").build());

        assertThat(uploadResponse.saved()).containsExactly(savedCard);
        assertThat(uploadResponse.duplicates()).containsExactly(Card.builder().front("f4").back("b4").build());
    }

    @Test
    void uploadCsv_ioExceptionIsLoggedAndRethrown() throws Exception {
        InputStream badStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("fail");
            }
        };

        assertThatThrownBy(() -> service.uploadCsv(badStream))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("fail");

        List<String> errorLogs = logCaptor.getErrorLogs();
        assertThat(errorLogs)
            .singleElement()
            .isEqualTo("CSV processing error");
    }
}
