package com.shrinker_kit.services;

import com.shrinker_kit.model.ClickStats;
import com.shrinker_kit.model.UrlMapping;
import com.shrinker_kit.repository.ClickStatsRepository;
import com.shrinker_kit.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UrlShortenerService {
    private static final String URL_CACHE_PREFIX = "url:";
    private static final String CLICK_COUNT_HASH = "clicks";
    
    @Autowired
    private UrlMappingRepository urlMappingRepository;
    
    @Autowired
    private ClickStatsRepository clickStatsRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String shortenUrl(String originalUrl) {
        long id = System.currentTimeMillis();
        String encoded = Base62.encode(id);
        String shortUrl = encoded.substring(0, Math.min(encoded.length(), 8));
        while (urlMappingRepository.existsByShortUrl(shortUrl)) {
            id = System.currentTimeMillis();
            encoded = Base62.encode(id);
            shortUrl = encoded.substring(0, Math.min(encoded.length(), 8));
        }

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMappingRepository.save(urlMapping);
        redisTemplate.opsForValue().set(URL_CACHE_PREFIX + shortUrl, originalUrl, 24, TimeUnit.HOURS);
        ClickStats clickStats = new ClickStats();
        clickStats.setShortUrl(shortUrl);
        clickStatsRepository.save(clickStats);
        redisTemplate.opsForHash().put(CLICK_COUNT_HASH, shortUrl, "0");
        return shortUrl;
    }

    @Transactional
    public String getOriginalUrl(String shortUrl) {
        redisTemplate.opsForHash().increment(CLICK_COUNT_HASH, shortUrl, 1);
        String cachedUrl = redisTemplate.opsForValue().get(URL_CACHE_PREFIX + shortUrl);
        updateClickStats(shortUrl);
        
        if (cachedUrl != null) {
            return cachedUrl;
        }

        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        String originalUrl = urlMapping.map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new RuntimeException("URL Not Found"));
        redisTemplate.opsForValue().set(URL_CACHE_PREFIX + shortUrl, originalUrl, 24, TimeUnit.HOURS);
        return originalUrl;
    }
    
    private void updateClickStats(String shortUrl) {
        LocalDateTime now = LocalDateTime.now();
        int updated = clickStatsRepository.incrementClickCount(shortUrl, now);
        if (updated == 0) {
            ClickStats clickStats = new ClickStats();
            clickStats.setShortUrl(shortUrl);
            clickStats.setClickCount(1L);
            clickStats.setLastAccessed(now);
            clickStatsRepository.save(clickStats);
        }
    }
    
    public Long getClickCount(String shortUrl) {
        Object cachedCount = redisTemplate.opsForHash().get(CLICK_COUNT_HASH, shortUrl);
        if (cachedCount != null) {
            return Long.parseLong(cachedCount.toString());
        }
        return clickStatsRepository.findByShortUrl(shortUrl)
                .map(ClickStats::getClickCount)
                .orElse(0L);
    }
}