package org.example.imagedownloader.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class PixabayService {

    @Value("${pixabay.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://pixabay.com/api/";

    private final RestTemplate restTemplate;

    public PixabayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> searchImages(
            String query,
            String imageType,
            String order,
            int perPage,
            int page,
            String orientation,
            String category,
            Integer minWidth,
            Integer minHeight,
            String colors,
            boolean editorsChoice,
            boolean safesearch) {

        // Validate perPage parameter (Pixabay API accepts values between 3 and 200)
        if (perPage < 3) {
            perPage = 3;
        } else if (perPage > 200) {
            perPage = 200;
        }

        if (page < 1) {
            page = 1;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("key", apiKey)
                .queryParam("q", query)
                .queryParam("image_type", imageType)
                .queryParam("order", order)
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .queryParam("safesearch", safesearch);

        // Add optional parameters
        if (orientation != null && !orientation.isEmpty()) {
            builder.queryParam("orientation", orientation);
        }
        if (category != null && !category.isEmpty()) {
            builder.queryParam("category", category);
        }
        if (minWidth != null) {
            builder.queryParam("min_width", minWidth);
        }
        if (minHeight != null) {
            builder.queryParam("min_height", minHeight);
        }
        if (colors != null && !colors.isEmpty()) {
            builder.queryParam("colors", colors);
        }
        if (editorsChoice) {
            builder.queryParam("editors_choice", true);
        }

        String url = builder.toUriString();
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request parameters: " + e.getMessage(), e);
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key", e);
            } else {
                throw new ResponseStatusException(e.getStatusCode(), "Error from Pixabay API: " + e.getMessage(), e);
            }
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to Pixabay API: " + e.getMessage(), e);
        }
    }
}
