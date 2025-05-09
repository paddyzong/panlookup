package com.cardlookup.panlookup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CardRangeDTO(
        long startRange,
        long endRange,
        String threeDSMethodURL
) {}
