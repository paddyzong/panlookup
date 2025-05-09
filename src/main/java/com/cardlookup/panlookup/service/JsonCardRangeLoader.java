package com.cardlookup.panlookup.service;

import com.cardlookup.panlookup.dto.CardRangeDTO;
import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.repository.CardRangeRepository;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
public class JsonCardRangeLoader {

    private static final Logger log = LoggerFactory.getLogger(JsonCardRangeLoader.class);

    private final CardRangeRepository repository;
    private final CardRangeService cardRangeService;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonCardRangeLoader(CardRangeRepository repository, CardRangeService cardRangeService) {
        this.repository = repository;
        this.cardRangeService = cardRangeService;
    }

    @Transactional
    public void loadFromFile(String path) throws Exception {
        log.info("Starting to load card ranges from file: {}", path);
        long startTime = System.currentTimeMillis();

        JsonFactory factory = mapper.getFactory();
        int count = 0;

        try (JsonParser parser = factory.createParser(new File(path))) {
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(token) && "cardRangeData".equals(parser.getCurrentName())) {
                    token = parser.nextToken();
                    if (token == JsonToken.START_ARRAY) {
                        while (parser.nextToken() == JsonToken.START_OBJECT) {
                            CardRangeDTO dto = mapper.readValue(parser, CardRangeDTO.class);
                            repository.save(new CardRange(null, dto.startRange(), dto.endRange(), dto.threeDSMethodURL()));
                            count++;
                            if (count % 10_000 == 0) {
                                log.info("Loaded {} records...", count);
                            }
                        }
                    }
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Finished loading {} card ranges in {} ms", count, duration);
        cardRangeService.refreshCache();
        log.info("Cache refreshed after loading card ranges.");
    }
}
