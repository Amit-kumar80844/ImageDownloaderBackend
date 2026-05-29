package org.example.imagedownloader.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.imagedownloader.service.PixabayService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") // allow Android app access
@Slf4j
public class PixabayController {

    private final PixabayService pixabayService;

    public PixabayController(PixabayService pixabayService) {
        this.pixabayService = pixabayService;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchImages(
            @RequestParam(defaultValue = "nature") String q,
            @RequestParam(defaultValue = "all") String image_type,
            @RequestParam(defaultValue = "popular") String order,
            @RequestParam(defaultValue = "20") int per_page,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String orientation,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer min_width,
            @RequestParam(required = false) Integer min_height,
            @RequestParam(required = false) String colors,
            @RequestParam(defaultValue = "false") boolean editors_choice,
            @RequestParam(defaultValue = "false") boolean safesearch,
            @RequestParam(defaultValue = "normal") String mode // NEW param: normal | wallpapers | high-quality
    ) {
        String finalImageType = image_type;
        String finalOrientation = orientation;
        Integer finalMinWidth = min_width;
        Integer finalMinHeight = min_height;
        boolean finalSafesearch = safesearch;
        boolean finalEditorsChoice = editors_choice;
        String finalOrder = order;

        if (mode != null) {
            switch (mode.toLowerCase()) {
                case "wallpapers":
                    finalImageType = "photo";
                    finalOrientation = (orientation != null) ? orientation : "horizontal";
                    finalMinWidth = (min_width != null) ? min_width : 1920;
                    finalMinHeight = (min_height != null) ? min_height : 1080;
                    finalSafesearch = true;
                    finalEditorsChoice = false;
                    finalOrder = (order != null) ? order : "popular";
                    break;

                case "high-quality":
                    finalImageType = "photo";
                    finalMinWidth = (min_width != null) ? min_width : 2000;
                    finalMinHeight = (min_height != null) ? min_height : 2000;
                    finalEditorsChoice = true;
                    finalSafesearch = true;
                    finalOrder = (order != null) ? order : "latest";
                    break;

                default:
                    break;
            }
        }

        Map<String, Object> results = pixabayService.searchImages(
                q, finalImageType, finalOrder, per_page, page,
                finalOrientation, category, finalMinWidth, finalMinHeight,
                colors, finalEditorsChoice, finalSafesearch
        );

        return ResponseEntity.ok(results);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadImage(@RequestParam String imageUrl) {
        try {
            URL url = URI.create(imageUrl).toURL();
            String host = url.getHost();
            
            // SSRF Protection: Validate host is Pixabay
            if (host == null || (!host.equalsIgnoreCase("pixabay.com") && !host.toLowerCase().endsWith(".pixabay.com"))) {
                log.warn("Blocked potential SSRF download request for host: {}", host);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            URLConnection connection = url.openConnection();
            // Set browser-like user agent to prevent Cloudflare/CDN blocks (403 Forbidden)
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            
            InputStream inputStream = connection.getInputStream();

            String filename = "pixabay_image_" + System.currentTimeMillis() + ".jpg";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            log.error("Failed to download image from URL: {}", imageUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}