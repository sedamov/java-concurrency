package local.practice.interview.url_shortener;

import local.practice.interview.util.UrlShortenerTaskDefinitions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UrlShortenerTest {

    private final UrlShortener urlShortener = new UrlShortener();

    @BeforeEach
    public void clearCache() {
        urlShortener.clearCache();
    }

    //Test cases for shortenUrl method
    @Test
    public void shortenUrlWithValidLongUrl() {
        String validUrl = UrlShortenerTaskDefinitions.VALID_BASE_URL + "somePath/testing/21265r3728";
        urlShortener.shortenUrl(validUrl);
        Map<String, String> longToShorUrlMapping = urlShortener.getLongToShortUrlMappings();
        Map<String, String> shortToLongUrlMapping = urlShortener.getShortToLongUrlMappings();

        assertFalse(longToShorUrlMapping.isEmpty(), "Long to short URL mapping should not be empty");
        assertFalse(shortToLongUrlMapping.isEmpty(), "Short to long URL mapping should not be empty");
        assertEquals(1, longToShorUrlMapping.size(), "only 1 Long to Short URL mapping should exist");
        assertEquals(1, shortToLongUrlMapping.size(), "only 1 Short to Long URL mapping should exist");
        String shortUrl = longToShorUrlMapping.get(validUrl);
        assertNotNull(shortUrl, "Long URL mapping not found");
        assertTrue(shortUrl.startsWith(UrlShortenerTaskDefinitions.VALID_BASE_URL), "Base URL has been changed");
        assertNotNull(shortToLongUrlMapping.get(shortUrl), "Short URL mapping not created");
    }

    @Test
    public void shortenUrlWithDuplicateLongUrl() {
        String validUrl = UrlShortenerTaskDefinitions.VALID_BASE_URL + "somePath/testing/453298asnr/sdt-njkdcs";
        urlShortener.shortenUrl(validUrl);
        urlShortener.shortenUrl(validUrl);
        Map<String, String> longToShorUrlMapping = urlShortener.getLongToShortUrlMappings();
        Map<String, String> shortToLongUrlMapping = urlShortener.getShortToLongUrlMappings();

        assertFalse(longToShorUrlMapping.isEmpty(), "Long to short URL mapping should not be empty");
        assertFalse(shortToLongUrlMapping.isEmpty(), "Short to long URL mapping should not be empty");
        assertEquals(1, longToShorUrlMapping.size(), "only 1 Long to Short URL mapping should exist");
        assertEquals(1, shortToLongUrlMapping.size(), "only 1 Short to Long URL mapping should exist");
        String shortUrl = longToShorUrlMapping.get(validUrl);
        assertNotNull(shortUrl, "Long URL mapping not found");
        assertTrue(shortUrl.startsWith(UrlShortenerTaskDefinitions.VALID_BASE_URL), "Base URL has been changed");
        assertNotNull(shortToLongUrlMapping.get(shortUrl), "Short URL mapping not created");
    }

    @Test
    public void shortenUrlWithInvalidLongUrl() {
        String invalidUrl = UrlShortenerTaskDefinitions.INVALID_BASE_URL + "abdhjsd/dhisuf";
        assertThrows(IllegalArgumentException.class, () -> urlShortener.shortenUrl(invalidUrl));
    }

    @Test
    public void shortenUrlNullUrl() {
        assertThrows(IllegalArgumentException.class, () -> urlShortener.shortenUrl(null));
    }

    @Test
    public void shortenUrlEmptyUrl() {
        assertThrows(IllegalArgumentException.class, () -> urlShortener.shortenUrl(""));
    }

    //Test cases for getLongUrl
    @Test
    public void getLongUrlForExistingShortUrl() {
        String validUrl = UrlShortenerTaskDefinitions.VALID_BASE_URL + "somePath/testing/453298asnr/sdt-njkdcs";
        urlShortener.shortenUrl(validUrl);
        Map<String, String> longToShorUrlMapping = urlShortener.getLongToShortUrlMappings();

        String mappedLongUrl = urlShortener.getLongUrl(longToShorUrlMapping.get(validUrl));
        assertNotNull(mappedLongUrl, "Long URL was not mapped");
        assertEquals(validUrl, mappedLongUrl, "Long URL was not mapped correctly");
    }

    @Test
    public void getLongUrlForNonExistentShortUrl() {
        urlShortener.shortenUrl(UrlShortenerTaskDefinitions.VALID_BASE_URL + "something-important");
        assertThrows(IllegalStateException.class, () -> urlShortener.getLongUrl(UrlShortenerTaskDefinitions.VALID_BASE_URL + "shortStuff"));
    }

    @Test
    public void getLongUrlFromEmptyMapping() {
        assertThrows(IllegalStateException.class, () -> urlShortener.getLongUrl(UrlShortenerTaskDefinitions.VALID_BASE_URL + "shortStuff"));
    }

    @Test
    public void getLongUrlForNullShortUrl() {
        assertThrows(IllegalArgumentException.class, () -> urlShortener.getLongUrl(null));
    }

    @Test
    public void getLongUrlForBlankShortUrl() {
        assertThrows(IllegalArgumentException.class, () -> urlShortener.getLongUrl(""));
    }

    //Test cases for removeUrl

    @Test
    public void removeUrlValidShortUrl() {
        String validUrl = UrlShortenerTaskDefinitions.VALID_BASE_URL + "somePath/testing/453298asnr/sdt-njkdcs";
        urlShortener.shortenUrl(validUrl);
        Map<String, String> longToShorUrlMapping = urlShortener.getLongToShortUrlMappings();

        urlShortener.removeUrl(longToShorUrlMapping.get(validUrl));
        Map<String, String> longToShorUrlMappingAfterRemoval = urlShortener.getLongToShortUrlMappings();
        Map<String, String> shortToLongUrlMappingAfterRemoval = urlShortener.getShortToLongUrlMappings();

        assertTrue(longToShorUrlMappingAfterRemoval.isEmpty(), "Long to short mapping was not removed");
        assertTrue(shortToLongUrlMappingAfterRemoval.isEmpty(), "Short to Long mapping was not removed");
    }

    @Test
    public void removeUrlWithNonExistentShortUrl() {
        String validUrl = UrlShortenerTaskDefinitions.VALID_BASE_URL + "somePath/testing/453298asnr/sdt-njkdcs";
        urlShortener.shortenUrl(validUrl);
        assertThrows(IllegalStateException.class, () -> urlShortener.removeUrl(validUrl));
    }

    @Test
    public void removeUrlFromEmptyMapping() {
        assertThrows(IllegalStateException.class, () -> urlShortener.removeUrl(UrlShortenerTaskDefinitions.VALID_BASE_URL + "someImportantThing"));
    }

    @Test
    public void removeUrlWithNullShortUrl() {
        assertThrows(IllegalArgumentException.class, () -> urlShortener.removeUrl(null));
    }

    @Test
    public void removeUrlWithBlankShortUrl() {
        assertThrows(IllegalArgumentException.class, () -> urlShortener.removeUrl(""));
    }
}
