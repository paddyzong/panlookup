package com.cardlookup.panlookup.service;

import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.repository.CardRangeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Transactional
public class CardRangeService {

    private static final Logger log = LoggerFactory.getLogger(CardRangeService.class);

    private final CardRangeRepository repo;
    private volatile TreeMap<Long, CardRange> cache = new TreeMap<>();

    public CardRangeService(CardRangeRepository repo) {
        this.repo = repo;
    }
//    @PostConstruct
//    public void init() {
//        log.info(this.toString());
//        refreshCache();
//    }

    public Optional<CardRange> findByPan(String pan) {
        long panLong;
        try {
            panLong = Long.parseLong(pan);
        } catch (NumberFormatException e) {
            log.error("error:",e);
            return Optional.empty();
        }

        Map.Entry<Long, CardRange> floor = cache.floorEntry(panLong);
        if (floor != null) {
            CardRange range = floor.getValue();
            if (panLong <= range.getEndBin()) {
                log.info("Found in cache!");
                return Optional.of(range);
            }
        }

        return repo.findByPan(panLong);
    }
    public Map<Long, CardRange> getCache() {
        return cache;
    }

    @Scheduled(fixedDelayString = "${cache.refresh-ms:600000}")
    public void refreshCache() {
        TreeMap<Long, CardRange> newCache = new TreeMap<>();
        repo.findAll().forEach(cr -> newCache.put(cr.getStartBin(), cr));
        cache = newCache;
        log.info("Card range cache refreshed with {} entries", cache.size());
    }
}
