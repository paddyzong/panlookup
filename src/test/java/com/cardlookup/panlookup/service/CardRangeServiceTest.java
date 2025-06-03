package com.cardlookup.panlookup.service;

import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.repository.CardRangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CardRangeServiceTest {

    @Autowired
    private CardRangeRepository repository;

    @Autowired
    private CardRangeService service;

    @Value("${cardrange.load-on-startup:false}")
    private boolean loadOnStartup;
    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void testFindByPan_ValidPan() {
        // Arrange
        CardRange cardRange = new CardRange(null, 100000L, 200000L, "https://example.com");
        repository.save(cardRange);

        // Act
        Optional<CardRange> result = service.findByPan("150000");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStartBin()).isEqualTo(100000L);
        assertThat(result.get().getEndBin()).isEqualTo(200000L);
    }

    @Test
    void testFindByPan_InvalidPan() {
        // Act
        Optional<CardRange> result = service.findByPan("invalid");

        // Assert
        assertThat(result).isEmpty();
    }
    @Test
    void testFindFloorStartBin() {
        service.setStartBinCache(List.of(100000L, 200000L, 300000L, 400000L));

        assertThat(service.findFloorStartBin(350000L)).isEqualTo(300000L);
        assertThat(service.findFloorStartBin(100000L)).isEqualTo(100000L);
        assertThat(service.findFloorStartBin(99999L)).isNull();
    }
    @Test
    void testRefreshCache() {
        CardRange cardRange1 = new CardRange(null, 100000L, 200000L, "https://example.com");
        CardRange cardRange2 = new CardRange(null, 300000L, 400000L, "https://example.org");
        repository.save(cardRange1);
        repository.save(cardRange2);

        service.setCacheEnabled(true);  // ensure cache is enabled

        service.refreshCache();

        long[] expectedBins = new long[] {100000L, 300000L};
        assertThat(service.getStartBinCache()).containsExactly(expectedBins);
    }


}