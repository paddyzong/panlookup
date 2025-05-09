package com.cardlookup.panlookup.service;

import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.repository.CardRangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonCardRangeLoaderDataJpaTest {

    @Autowired
    private CardRangeRepository repository;

    @Autowired
    private JsonCardRangeLoader loader;
    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }
    @Test
    void testLoadFromFile() throws Exception {
        // Arrange
        File tempFile = File.createTempFile("card_ranges", ".json");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("""
                {
                  "cardRangeData": [
                    {
                      "startRange": 100000,
                      "endRange": 200000,
                      "threeDSMethodURL": "https://example.com"
                    },
                    {
                      "startRange": 300000,
                      "endRange": 400000,
                      "threeDSMethodURL": "https://example.org"
                    }
                  ]
                }
                """);
        }

        // Act
        loader.loadFromFile(tempFile.getAbsolutePath());

        // Assert
        List<CardRange> savedRanges = repository.findAll();
        assertThat(savedRanges).hasSize(2);
        assertThat(savedRanges.get(0).getStartBin()).isEqualTo(100000L);
        assertThat(savedRanges.get(0).getEndBin()).isEqualTo(200000L);
        assertThat(savedRanges.get(0).getThreeDSMethodUrl()).isEqualTo("https://example.com");
        assertThat(savedRanges.get(1).getStartBin()).isEqualTo(300000L);
        assertThat(savedRanges.get(1).getEndBin()).isEqualTo(400000L);
        assertThat(savedRanges.get(1).getThreeDSMethodUrl()).isEqualTo("https://example.org");
    }
}