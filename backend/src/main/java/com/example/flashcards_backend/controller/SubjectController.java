package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.SubjectRequest;
import com.example.flashcards_backend.dto.SubjectDto;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.service.CurrentUserService;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    public static final String REQUEST_MAPPING = "/subjects/";
    private final SubjectService subjectService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Get all subjects", description = "Returns all subjects for a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubjectDto.class)
                    ))
    })
    @GetMapping
    public ResponseEntity<List<SubjectDto>> getAllForUser(@AuthenticationPrincipal Jwt jwt) {
        User currentUser = currentUserService.getCurrentUser(jwt);
        return ResponseEntity.ok(subjectService.findByUserId(currentUser.getId()).stream()
                .map(SubjectDto::fromEntity).toList());
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
    public ResponseEntity<SubjectDto> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        currentUserService.getCurrentUser(jwt);
        return ResponseEntity.ok(SubjectDto.fromEntity(subjectService.findById(id)));
    }

    @Operation(summary = "Create a new subject", description = "Creates a new subject.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subject created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubjectDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<SubjectDto> create(
            @RequestBody SubjectRequest subjectRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        User currentUser = currentUserService.getCurrentUser(jwt);
        Subject subject = subjectService.create(subjectRequest, currentUser.getId());
        return ResponseEntity
                .created(URI.create(REQUEST_MAPPING + subject.getId()))
                .body(SubjectDto.fromEntity(subject));
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
    public ResponseEntity<SubjectDto> update(
            @PathVariable Long id,
            @RequestBody SubjectRequest subjectRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        currentUserService.getCurrentUser(jwt);
        return ResponseEntity.ok(SubjectDto.fromEntity(subjectService.update(id, subjectRequest)));
    }

    @Operation(summary = "Delete subject", description = "Deletes a subject by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subject deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        currentUserService.getCurrentUser(jwt);
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
