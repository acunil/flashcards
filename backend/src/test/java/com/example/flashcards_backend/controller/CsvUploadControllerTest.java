package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.service.CsvUploadServiceImpl;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CsvUploadController.class)
class CsvUploadControllerTest {

    public static final String PATH = "/api/upload";

    @MockitoBean
    private CsvUploadServiceImpl csvUploadService;

    @Autowired
    private MockMvc mockMvc;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(CsvUploadController.class);
    }

    @AfterEach
    void tearDown() {
        logCaptor.close();
    }

    @Test
    void uploadCsv_noFileProvided_returnsBadRequestAndLogsError() throws Exception {
        MockMultipartFile empty = new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, new byte[0]);
        mockMvc.perform(multipart(PATH).file(empty))
            .andExpect(status().isBadRequest());
        assertThat(logCaptor.getErrorLogs())
            .singleElement()
            .isEqualTo("CSV upload failed: no file provided");
    }

    @Test
    void uploadCsv_serviceThrows_returnsInternalServerErrorAndLogsError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "a,b".getBytes());
        doThrow(new RuntimeException("Expected test exception"))
            .when(csvUploadService).uploadCsv(any(InputStream.class));
        mockMvc.perform(multipart(PATH)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isInternalServerError());
        assertThat(logCaptor.getErrorLogs())
            .singleElement()
            .isEqualTo("Error importing CSV");
    }

    @Test
    void uploadCsv_validFile_returnsOkWithResponseBody() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "a,b".getBytes());
        CsvUploadResponseDto response = CsvUploadResponseDto.builder()
            .saved(List.of())
            .duplicates(List.of())
            .build();
        when(csvUploadService.uploadCsv(any(InputStream.class))).thenReturn(response);
        mockMvc.perform(multipart(PATH)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.saved").isArray())
            .andExpect(jsonPath("$.duplicates").isArray());
        assertThat(logCaptor.getErrorLogs()).isEmpty();
    }
}
