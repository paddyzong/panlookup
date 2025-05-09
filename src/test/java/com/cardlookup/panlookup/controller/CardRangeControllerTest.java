package com.cardlookup.panlookup.controller;

import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.service.CardRangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardRangeController.class)
class CardRangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardRangeService service;

    @BeforeEach
    void setUp() {
        Mockito.reset(service);
    }

    @Test
    void testFind_ValidPan() throws Exception {
        // Arrange
        CardRange cardRange = new CardRange(null, 1000000000000000L, 2000000000000000L, "https://example.com");
        when(service.findByPan("1500000000000000")).thenReturn(Optional.of(cardRange));

        // Act & Assert
        mockMvc.perform(get("/api/card-range/1500000000000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.cardRange.startBin").value(1000000000000000L))
                .andExpect(jsonPath("$.cardRange.endBin").value(2000000000000000L))
                .andExpect(jsonPath("$.cardRange.threeDSMethodUrl").value("https://example.com"));
    }

    @Test
    void testFind_InvalidPan() throws Exception {
        // Arrange
        when(service.findByPan("8000000000000000")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/card-range/8000000000000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(false))
                .andExpect(jsonPath("$.cardRange").doesNotExist());
    }
}
