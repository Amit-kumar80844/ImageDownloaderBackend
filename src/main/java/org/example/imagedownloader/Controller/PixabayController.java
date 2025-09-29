package org.example.imagedownloader.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
        return restTemplate.getForObject(url, Map.class);
    }
}