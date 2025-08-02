package com.example.flashcards_backend.dto;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

class CsvUploadResponseDtoTest {
    @Test
    void testCsvUploadResponseDtoCreation() {
        CsvUploadResponseDto response = CsvUploadResponseDto.builder()
                .saved(List.of())
                .duplicates(List.of())
                .build();

        assertThat(response.saved()).isEmpty();
        assertThat(response.duplicates()).isEmpty();
    }
}