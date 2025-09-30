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
        switch (mode.toLowerCase()) {
            case "wallpapers":
                image_type = "photo";
                orientation = (orientation != null) ? orientation : "horizontal";
                min_width = (min_width != null) ? min_width : 1920;
                min_height = (min_height != null) ? min_height : 1080;
                safesearch = true;
                editors_choice = false;
                order = (order != null) ? order : "popular";
                break;

            case "high-quality":
                image_type = "photo";
                min_width = (min_width != null) ? min_width : 2000;
                min_height = (min_height != null) ? min_height : 2000;
                editors_choice = true;
                safesearch = true;
                order = (order != null) ? order : "latest";
                break;

            default:
                break;
        }

        Map<String, Object> results = pixabayService.searchImages(
                q, image_type, order, per_page, page,
                orientation, category, min_width, min_height,
                colors, editors_choice, safesearch
        );

        return ResponseEntity.ok(results);
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, String>> getCategories() {
        Map<String, String> categories = Map.ofEntries(
                Map.entry("backgrounds", "Backgrounds"),
                Map.entry("fashion", "Fashion"),
                Map.entry("nature", "Nature"),
                Map.entry("science", "Science"),
                Map.entry("education", "Education"),
                Map.entry("feelings", "Feelings"),
                Map.entry("health", "Health"),
                Map.entry("people", "People"),
                Map.entry("religion", "Religion"),
                Map.entry("places", "Places"),
                Map.entry("animals", "Animals"),
                Map.entry("industry", "Industry"),
                Map.entry("computer", "Computer"),
                Map.entry("food", "Food"),
                Map.entry("sports", "Sports"),
                Map.entry("transportation", "Transportation"),
                Map.entry("travel", "Travel"),
                Map.entry("buildings", "Buildings"),
                Map.entry("business", "Business"),
                Map.entry("music", "Music")
        );
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/colors")
    public ResponseEntity<Map<String, String>> getColors() {
        Map<String, String> colors = Map.ofEntries(
                Map.entry("grayscale", "Grayscale"),
                Map.entry("transparent", "Transparent"),
                Map.entry("red", "Red"),
                Map.entry("orange", "Orange"),
                Map.entry("yellow", "Yellow"),
                Map.entry("green", "Green"),
                Map.entry("turquoise", "Turquoise"),
                Map.entry("blue", "Blue"),
                Map.entry("lilac", "Lilac"),
                Map.entry("pink", "Pink"),
                Map.entry("white", "White"),
                Map.entry("gray", "Gray"),
                Map.entry("black", "Black"),
                Map.entry("brown", "Brown")
        );
        return ResponseEntity.ok(colors);
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