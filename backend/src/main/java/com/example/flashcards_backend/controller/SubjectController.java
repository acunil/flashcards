package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.SubjectDto;
import com.example.flashcards_backend.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "Get all subjects", description = "Returns all subjects for a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubjectDto.class)
                    ))
    })
    @GetMapping
    public List<SubjectDto> getAllForUser(@RequestParam UUID userId) {
        return subjectService.findByUserId(userId).stream().map(SubjectDto::fromEntity).toList();
    }

    @Operation(summary = "Get subject by ID", description = "Returns a subject by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubjectDto.class))),
            @ApiResponse(responseCode = "404", description = "Subject not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(SubjectDto.fromEntity(subjectService.findById(id)));
    }

    @Operation(summary = "Create a new subject", description = "Creates a new subject.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subject created successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody SubjectDto subject,
            @RequestParam UUID userId) {
        subjectService.create(subject);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update subject", description = "Updates an existing subject by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subject updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubjectDto.class))),
            @ApiResponse(responseCode = "404", description = "Subject not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDto> update(@PathVariable Long id, @RequestBody SubjectDto subject) {
        try {
            return ResponseEntity.ok(SubjectDto.fromEntity(subjectService.update(id, subject)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete subject", description = "Deletes a subject by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subject deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
