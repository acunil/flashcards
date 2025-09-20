package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.service.CsvExportService;
import com.example.flashcards_backend.service.CsvExportService.CardSource;
import com.example.flashcards_backend.service.CurrentUserService;
import com.example.flashcards_backend.service.DeckService;
import com.example.flashcards_backend.service.SubjectService;
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
    private final SubjectService subjectService;
    private final DeckService deckService;

    @Operation(summary = "Exports all cards from a subject")
    @ApiResponse(responseCode = "404", description = "Subject not found")
    @GetMapping(path = "/subject/{subjectId}")
    public ResponseEntity<byte[]> exportSubjectCsv(@PathVariable Long subjectId, @AuthenticationPrincipal Jwt jwt) throws IOException {
        currentUserService.getCurrentUser(jwt);
      byte[] bytes = csvExportService.exportCards(CardSource.SUBJECT, subjectId);
      return createCsvResponse(CardSource.SUBJECT, subjectId, bytes);
    }

    @Operation(summary = "Exports all cards from a deck")
    @ApiResponse(responseCode = "404", description = "Deck not found")
    @GetMapping(path = "/deck/{deckId}")
    public ResponseEntity<byte[]> exportDeckCsv(@PathVariable Long deckId, @AuthenticationPrincipal Jwt jwt) throws IOException {
        currentUserService.getCurrentUser(jwt);
      byte[] bytes = csvExportService.exportCards(CardSource.DECK, deckId);
      return createCsvResponse(CardSource.DECK, deckId, bytes);
    }

    /* HELPER METHOD */
  private ResponseEntity<byte[]> createCsvResponse(CardSource source, Long id, byte[] csv) {
    String sourceName = switch (source) {
      case SUBJECT -> subjectService.findById(id).getName();
      case DECK -> deckService.getDeckById(id).getName();
    };
    String filename = source.name().toLowerCase() + "_" + sourceName + ".csv";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("text/csv"));
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
    return ResponseEntity.ok()
        .headers(headers)
        .body(csv);
  }
}
