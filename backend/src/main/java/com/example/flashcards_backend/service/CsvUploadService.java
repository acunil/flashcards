package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CsvUploadResponseDto;

import java.io.IOException;
import java.io.InputStream;

public interface CsvUploadService {
    CsvUploadResponseDto uploadCsv(InputStream csvStream, Long subjectId) throws IOException;
}