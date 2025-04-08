package com.shrinker_kit.repository;

import com.shrinker_kit.model.ClickStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ClickStatsRepository extends JpaRepository<ClickStats, Long> {
    Optional<ClickStats> findByShortUrl(String shortUrl);
    
    @Transactional
    @Modifying
    @Query("UPDATE ClickStats c SET c.clickCount = c.clickCount + 1, c.lastAccessed = ?2 WHERE c.shortUrl = ?1")
    int incrementClickCount(String shortUrl, LocalDateTime accessTime);
}