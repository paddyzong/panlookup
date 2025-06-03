package com.cardlookup.panlookup.service;

import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.repository.CardRangeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

@Service
public class CardRangeService {

    private static final Logger log = LoggerFactory.getLogger(CardRangeService.class);

    private final CardRangeRepository repo;
    private final StringRedisTemplate redisTemplate;
    private volatile List<Long> startBinCache = new ArrayList<>();

    @Value("${cardrange.cache-enabled:false}")
    private boolean cacheEnabled;

    public CardRangeService(CardRangeRepository repo, StringRedisTemplate redisTemplate) {
        this.repo = repo;
        this.redisTemplate = redisTemplate;
    }

    public void setStartBinCache(List<Long> startBinCache) {
        this.startBinCache = startBinCache;
    }

    public List<Long> getStartBinCache() {
        return startBinCache;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public Optional<CardRange> findByPan(String pan) {
        long panLong;
        try {
            panLong = Long.parseLong(pan);
        } catch (NumberFormatException e) {
            log.error("Invalid PAN:", e);
            return Optional.empty();
        }

        if (cacheEnabled) {
            Long floorStartBin = findFloorStartBin(panLong);

            if (floorStartBin != null) {
                String redisKey = "cardrange:" + floorStartBin;
                Map<Object, Object> fields = redisTemplate.opsForHash().entries(redisKey);
                if (!fields.isEmpty()) {
                    try {
                        long start = Long.parseLong((String) fields.get("startBin"));
                        long end = Long.parseLong((String) fields.get("endBin"));
                        String url = (String) fields.get("threeDSMethodURL");

                        if (panLong <= end) {
                            CardRange cached = new CardRange(start, end, url); // or builder/setter
                            log.info("Found in Redis HASH record!");
                            return Optional.of(cached);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to deserialize CardRange from Redis", e);
                    }
                }
            }
        }

        // Fallback to DB
        Optional<CardRange> dbResult = repo.findByPan(panLong);
        if(dbResult.isPresent()){
            dbResult.get().setId(null);
        }
        if (cacheEnabled && dbResult.isPresent()) {
            try {
                CardRange cr = dbResult.get();
                String key = "cardrange:" + cr.getStartBin();
                redisTemplate.opsForHash().put(key, "startBin", String.valueOf(cr.getStartBin()));
                redisTemplate.opsForHash().put(key, "endBin", String.valueOf(cr.getEndBin()));
                redisTemplate.opsForHash().put(key, "threeDSMethodURL", cr.getThreeDSMethodUrl());
                redisTemplate.expire(key, Duration.ofMinutes(90));
            } catch (Exception e) {
                log.warn("Failed to serialize CardRange to Redis", e);
            }
        }

        return dbResult;
    }


    public Long findFloorStartBin(long pan) {
        List<Long> sortedStartBins = startBinCache;
        int low = 0;
        int high = sortedStartBins.size() - 1;
        Long result = null;

        while (low <= high) {
            int mid = (low + high) / 2;
            long midVal = sortedStartBins.get(mid);

            if (midVal == pan) {
                return midVal; // exact match
            } else if (midVal < pan) {
                result = midVal;   // possible floor, but search right
                low = mid + 1;
            } else {
                high = mid - 1; // search left
            }
        }

        return result; // may be null if no value ≤ cardNumber
    }

    @Scheduled(fixedDelayString = "${cache.refresh-ms:600000}")
    public void refreshCache() {
        if (!cacheEnabled) {
            log.info("Cache refresh skipped (disabled)");
            return;
        }
        this.setStartBinCache(repo.findAllStartBins());
        log.info("Card range cache refreshed with {} entries", startBinCache.size());
    }
}
