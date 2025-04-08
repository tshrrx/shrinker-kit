package com.shrinker_kit.shrinker_kit;

import com.shrinker_kit.services.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UrlController {
    
    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody Map<String, String> request) {
        String originalUrl = request.get("url");
        String shortUrl = urlShortenerService.shortenUrl(originalUrl);
        return ResponseEntity.ok(Map.of(
            "originalUrl", originalUrl,
            "shortUrl", shortUrl
        ));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl) {
        String originalUrl = urlShortenerService.getOriginalUrl(shortUrl);
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }
    
    @GetMapping("/stats/{shortUrl}")
    public ResponseEntity<Map<String, Object>> getUrlStats(@PathVariable String shortUrl) {
        Long clickCount = urlShortenerService.getClickCount(shortUrl);
        return ResponseEntity.ok(Map.of(
            "shortUrl", shortUrl,
            "clickCount", clickCount
        ));
    }
}