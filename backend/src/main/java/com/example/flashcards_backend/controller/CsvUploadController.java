package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.service.CsvUploadServiceImpl;
import com.example.flashcards_backend.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@AllArgsConstructor
@RestController(value = "csvUploadController")
@RequestMapping("/csv")
@Slf4j
public class CsvUploadController {
    public static final String CSV_UPLOAD_FORMAT = """
        CSV file to upload. Format: front,back,decks. Headers required. ; = separator for decks.
        \s
        Example:
        \s
        front,back,decks
        die Katze,cat,Animals;German Basics
        das Haus,house,Buildings;German Basics
    """;

    private final CsvUploadServiceImpl csvUploadService;
    private final CurrentUserService currentUserService;

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
    public ResponseEntity<CsvUploadResponseDto> uploadCsv(
            @Parameter(
                    description = CSV_UPLOAD_FORMAT,
                    required = true,
                    content = @Content(mediaType = "text/csv")
            )
            @RequestBody MultipartFile file,
            @PathVariable("subjectId") Long subjectId,
            @AuthenticationPrincipal Jwt jwt
            ) {
        currentUserService.getCurrentUser(jwt);
        log.info("CSV upload for subject {}", subjectId);
        if (Objects.isNull(file) || file.isEmpty()) {
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
