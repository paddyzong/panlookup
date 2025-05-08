package com.cardlookup.panlookup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PresMessageDTO(
        long startRange,
        long endRange,
        String threeDSMethodURL
) {}
