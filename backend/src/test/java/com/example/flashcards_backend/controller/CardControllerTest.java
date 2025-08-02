package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardDto;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAll_returnsListOfCardDto() throws Exception {
        Card c1 = Card.builder().id(1L).front("f1").back("b1").build();
        Card c2 = Card.builder().id(2L).front("f2").back("b2").build();
        when(cardService.getAll()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/cards"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].front").value("f1"))
            .andExpect(jsonPath("$[0].back").value("b1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].front").value("f2"))
            .andExpect(jsonPath("$[1].back").value("b2"));
    }

    @Test
    void getById_existingId_returnsCardDto() throws Exception {
        Card c = Card.builder().id(1L).front("front").back("back").build();
        when(cardService.getById(1L)).thenReturn(c);

        mockMvc.perform(get("/api/cards/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.front").value("front"))
            .andExpect(jsonPath("$.back").value("back"));
    }

    @Test
    void getById_missingId_returnsNotFound() throws Exception {
        when(cardService.getById(99L)).thenThrow(new CardNotFoundException(99L));

        mockMvc.perform(get("/api/cards/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void create_validDto_returnsCreatedWithLocationAndBody() throws Exception {
        Card created = Card.builder().id(10L).front("f").back("b").build();
        when(cardService.create(any())).thenReturn(created);

        String json = """
            {"id":null,"front":"f","back":"b"}
            """;

        mockMvc.perform(post("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/cards/10"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.front").value("f"))
            .andExpect(jsonPath("$.back").value("b"));
    }

    @Test
    void update_existingId_returnsNoContent() throws Exception {
        String json = """
            {"id":null,"front":"newF","back":"newB"}
            """;

        mockMvc.perform(put("/api/cards/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNoContent());

        verify(cardService).update(5L, new CardDto(null, "newF", "newB"));
    }

    @Test
    void update_missingId_returnsNotFound() throws Exception {
        doThrow(new CardNotFoundException(7L)).when(cardService).update(eq(7L), any());

        String json = """
            {"id":null,"front":"x","back":"y"}
            """;

        mockMvc.perform(put("/api/cards/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound());
    }
}
