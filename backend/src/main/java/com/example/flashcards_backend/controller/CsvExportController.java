package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.service.CsvExportService;
import com.example.flashcards_backend.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UncheckedIOException;

@Tag(name = "CSV Export", description = "Export cards to importable csv format")
@AllArgsConstructor
@RestController(value = "csvExportController")
@ApiResponse(responseCode = "200", description = "Successful operation")
@ApiResponse(responseCode = "401", description = "Unauthorized")
@ApiResponse(responseCode = "500", description = "Internal server error")
@RequestMapping("/export")
@Slf4j
public class CsvExportController {

    private final CurrentUserService currentUserService;
    private final CsvExportService csvExportService;

    @Operation(summary = "Exports all cards from a subject")
    @ApiResponse(responseCode = "404", description = "Subject not found")
    @GetMapping(path = "/subject/{subjectId}")
    public ResponseEntity<byte[]> exportSubjectCsv(@PathVariable Long subjectId, @AuthenticationPrincipal Jwt jwt) {
        currentUserService.getCurrentUser(jwt);
        return exportCsv(() -> csvExportService.exportSubjectCards(subjectId), "subject-" + subjectId);
    }

    @Operation(summary = "Exports all cards from a deck")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    @GetMapping(path = "/deck/{deckId}")
    public ResponseEntity<byte[]> exportDeckCsv(@PathVariable Long deckId, @AuthenticationPrincipal Jwt jwt) {
        currentUserService.getCurrentUser(jwt);
        return exportCsv(() -> csvExportService.exportDeckCards(deckId), "deck-" + deckId);
    }

    private ResponseEntity<byte[]> exportCsv(SupplierWithIOException<byte[]> exporter, String filename) {
        try {
            byte[] csv = exporter.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".csv")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(csv);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @FunctionalInterface
    interface SupplierWithIOException<T> {
        T get() throws IOException;
    }


}
