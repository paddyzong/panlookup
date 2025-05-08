package com.cardlookup.panlookup.service;

import com.cardlookup.panlookup.dto.PresMessageDTO;
import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.repository.CardRangeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Transactional
public class CardRangeService {

    private final CardRangeRepository repo;
    private volatile TreeMap<Long, CardRange> cache = new TreeMap<>();

    public CardRangeService(CardRangeRepository repo) {
        this.repo = repo;
        refreshCache();
    }

    @Transactional(readOnly = true)
    public Optional<CardRange> findByPan(String pan) {
        long panLong;
        try {
            panLong = Long.parseLong(pan);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        Map.Entry<Long, CardRange> floor = cache.floorEntry(panLong);
        if (floor != null) {
            CardRange range = floor.getValue();
            if (panLong <= range.getEndBin()) {
                return Optional.of(range);
            }
        }

        return repo.findByPan(panLong);
    }

    @Scheduled(fixedDelayString = "${cache.refresh-ms:600000}")
    public final void refreshCache() {
        TreeMap<Long, CardRange> newCache = new TreeMap<>();
        repo.findAll().forEach(cr -> newCache.put(cr.getStartBin(), cr));
        cache = newCache;
    }
}
