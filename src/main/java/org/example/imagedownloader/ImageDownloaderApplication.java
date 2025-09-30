package org.example.imagedownloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ImageDownloaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageDownloaderApplication.class, args);
    }
}
