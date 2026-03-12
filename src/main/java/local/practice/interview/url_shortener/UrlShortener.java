package local.practice.interview.url_shortener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Design and implement a URL Shortener in Java that converts long URLs into short, unique aliases and allows retrieving
 * the original URL from the alias. The implementation should be thread-safe, handle edge cases, and include
 * comprehensive unit tests.
 * Requirements:
 * <p>
 * Create a class UrlShortener with the following methods:
 * <p>
 * 1. String shortenUrl(String longUrl): Takes a long URL (e.g., "https://www.example.com") and returns a short alias (e.g., "abc123").
 * The alias must be unique and short (e.g., 6–8 characters).
 * 2. String getLongUrl(String shortUrl): Takes a short alias and returns the original long URL. If the alias doesn’t exist, throw an appropriate exception.
 * 3. void removeUrl(String shortUrl): Removes the mapping for a given short alias. If the alias doesn’t exist, throw an exception.
 * <p>
 * <p>
 * Ensure the implementation is thread-safe to handle concurrent calls to all methods.
 * Use an appropriate data structure to store URL mappings (e.g., a map).
 * Handle edge cases:
 * <p>
 * 1. Invalid URLs (e.g., null, empty, malformed).
 * 2. Duplicate long URLs (should return the same short URL or create a new one, depending on design).
 * 3. Non-existent short URLs.
 * 4. Empty mapping store.
 */
public class UrlShortener {
    private static final String URL_REGEX =
            "^(https?://)?" + // Optional http or https protocol
                    "([a-zA-Z0-9-]+\\.)+" + // Domain name (e.g., example.)
                    "[a-zA-Z]{2,6}/?"; // Top-level domain (e.g., com, org, net)

    private final Pattern URL_PATTERN_MATCHER = Pattern.compile(URL_REGEX);

    private final ReentrantReadWriteLock explicitLock = new ReentrantReadWriteLock();

    private final ConcurrentHashMap<String, String> longToShortUrlMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> shortToLongUrlMap = new ConcurrentHashMap<>();

    private static final String BASE64_URL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    public String shortenUrl(String longUrl) {
        explicitLock.writeLock().lock();
        if (isNullOrBlank(longUrl)) throw new IllegalArgumentException("Provided URL can not be null or blank");

        String validBase = getValidBaseUrl(longUrl);
        if (validBase == null) throw new IllegalArgumentException("Provided URL is not a valid URL");
        String existingShortUrl = longToShortUrlMap.get(longUrl);
        StringBuilder shortUrlBuilder = new StringBuilder(validBase);
        if (existingShortUrl != null) {
            return existingShortUrl;
        } else {
            shortUrlBuilder.append(validBase.endsWith("/") ? "" : "/").append(generateRandomShortString());
            longToShortUrlMap.put(longUrl, shortUrlBuilder.toString());
            shortToLongUrlMap.put(shortUrlBuilder.toString(), longUrl);
        }
        explicitLock.writeLock().unlock();
        return shortUrlBuilder.toString();
    }

    public String getLongUrl(String shortUrl) {
        explicitLock.readLock().lock();
        try {
            if (isNullOrBlank(shortUrl)) throw new IllegalArgumentException("Provided short URL is either null or blank");
            String mappedLongUrl = shortToLongUrlMap.get(shortUrl);
            if (mappedLongUrl != null) return mappedLongUrl;
            throw new IllegalStateException("There is no long URL mapping found for provided short URL: " + shortUrl);
        } finally {
            explicitLock.readLock().unlock();
        }
    }

    public void removeUrl(String shortUrl) {
        explicitLock.writeLock().lock();
        try {
            if (isNullOrBlank(shortUrl)) throw new IllegalArgumentException("Provided short URL is either null or blank");
            String longUrl = shortToLongUrlMap.remove(shortUrl);
            if (longUrl == null) {
                throw new IllegalStateException("There is no long URL mapping found for provided short URL: " + shortUrl);
            } else {
              longToShortUrlMap.remove(longUrl);
            }
        } finally {
            explicitLock.writeLock().unlock();
        }
    }

    /**
     * This method is added for making the UrlShortener easier to test with unit tests.
     */
    public void clearCache() {
        explicitLock.writeLock().lock();
        longToShortUrlMap.clear();
        shortToLongUrlMap.clear();
        explicitLock.writeLock().unlock();
    }

    public Map<String, String> getLongToShortUrlMappings() {
        explicitLock.readLock().lock();
        try {
            return new HashMap<>(this.longToShortUrlMap);
        } finally {
            explicitLock.readLock().unlock();
        }
    }

    public Map<String, String> getShortToLongUrlMappings() {
        explicitLock.readLock().lock();
        try {
            return new HashMap<>(this.shortToLongUrlMap);
        } finally {
            explicitLock.readLock().unlock();
        }
    }

    private String generateRandomShortString() {
        StringBuilder randomStringBuilder = new StringBuilder(6);
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(BASE64_URL_CHARS.length());
            randomStringBuilder.append(BASE64_URL_CHARS.charAt(index));
        }
        return randomStringBuilder.toString();
    }

    private boolean isNullOrBlank(String url) {
        return url == null || url.isBlank();
    }

    /*
     * This method is both validating the provided URL and extracting the base part of the URL.
     * From the design point of view it is not a very good idea, but from the performance point of view it reduces the need
     * to match a pattern 2 times which is a heavy operation. So bad design is a trade-off for better performance.

     * Returns the matched part of the provided URL
     * @param url URL under test
     * @return the base part of the URL if provided URL is a valid URL and NULL if it is not.
     */
    private String getValidBaseUrl(String url) {
        Matcher urlMatcher = URL_PATTERN_MATCHER.matcher(url);
        if (urlMatcher.find()) {
            return urlMatcher.group();
        }
        return null;
    }

}

