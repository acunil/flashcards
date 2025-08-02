package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.service.CsvUploadServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@AllArgsConstructor
@RestController(value = "csvUploadController")
@RequestMapping("/api")
@Slf4j
public class CsvUploadController {
    private final CsvUploadServiceImpl csvUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvUploadResponseDto> uploadCsv(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            log.error("CSV upload failed: no file provided");
            return ResponseEntity.badRequest().build();
        }

        try (InputStream is = file.getInputStream()) {
            CsvUploadResponseDto response = csvUploadService.uploadCsv(is);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error importing CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
