package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.UploadResponse;

import java.io.IOException;
import java.io.InputStream;

public interface CsvUploadService {
    UploadResponse uploadCsv(InputStream csvStream) throws IOException;
}