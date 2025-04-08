package com.shrinker_kit.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urls")
public class UrlMapping {
    // public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }
    // public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    // public String getOriginalUrl() { return this.originalUrl; }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String shortUrl; 

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
