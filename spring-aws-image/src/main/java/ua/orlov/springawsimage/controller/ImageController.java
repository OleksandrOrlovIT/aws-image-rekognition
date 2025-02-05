package ua.orlov.springawsimage.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.orlov.springawsimage.service.ImageService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    @PostMapping(value = "/uploadImage", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        logger.info("Uploading image to S3: " + image.getOriginalFilename());

        if (!imageService.uploadImage(image)) {
            return ResponseEntity.status(400).body("Failed to upload photo");
        }

        return ResponseEntity.status(200).body("File uploaded successfully");
    }

    @GetMapping(value = "/get-by-filter")
    public ResponseEntity<List<String>> getImagesByFilter(@RequestParam("filterWord") String filterWord) {
        logger.info("Getting images by filter: " + filterWord);

        List<String> base64Images = imageService.getImagesByFilter(filterWord);

        return ResponseEntity.status(200).body(base64Images);
    }

}
