package org.example.imagedownloader.Controller;

import org.example.imagedownloader.service.PixabayService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") // allow Android app access
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

        Map<String, Object> results = pixabayService.searchImages(
                q, image_type, order, per_page, page,
                orientation, category, min_width, min_height,
                colors, editors_choice, safesearch
        );

        return ResponseEntity.ok(results);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadImage(@RequestParam String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();

            String filename = "pixabay_image_" + System.currentTimeMillis() + ".jpg";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}