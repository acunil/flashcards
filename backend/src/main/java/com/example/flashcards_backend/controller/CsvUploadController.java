package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.service.CsvUploadServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@AllArgsConstructor
@RestController(value = "csvUploadController")
@RequestMapping("/csv")
@Slf4j
public class CsvUploadController {
    private final CsvUploadServiceImpl csvUploadService;

    @Operation(summary = "Upload CSV file for card import",
               description = "Uploads a CSV file containing flashcards and processes it for import.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV file processed successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CsvUploadResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad request, no file provided",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error, could not process CSV",
            content = @Content(mediaType = "application/json"))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/{subjectId}")
    public ResponseEntity<CsvUploadResponseDto> uploadCsv(@RequestParam MultipartFile file, @PathVariable("subjectId") Long subjectId) {
        if (file.isEmpty()) {
            log.error("CSV upload failed: no file provided");
            return ResponseEntity.badRequest().build();
        }

        try (InputStream is = file.getInputStream()) {
            CsvUploadResponseDto response = csvUploadService.uploadCsv(is, subjectId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error importing CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
