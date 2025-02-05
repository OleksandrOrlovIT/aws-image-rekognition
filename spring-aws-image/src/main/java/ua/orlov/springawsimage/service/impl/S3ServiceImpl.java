package ua.orlov.springawsimage.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import ua.orlov.springawsimage.service.S3Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    @Value("${s3.bucket.name}")
    private String BUCKET_NAME;

    public String loadImageToS3Bucket(MultipartFile img) {
        try (S3Client s3Client = S3Client.builder().build()) {

            String uniqueKey = UUID.randomUUID() + "-" + img.getOriginalFilename();

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(uniqueKey)
                    .contentType(img.getContentType())
                    .build();

            RequestBody body = RequestBody.fromBytes(img.getBytes());
            s3Client.putObject(putRequest, body);

            String imageUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + uniqueKey;
            logger.info("Successfully uploaded: {}", imageUrl);

            return uniqueKey;

        } catch (Exception e) {
            logger.error("Upload failed: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public List<String> getImagesByObjectKeysFromS3(List<String> keys) {
        List<String> images = new ArrayList<>();

        for (String key : keys) {
            try (S3Client s3Client = S3Client.builder().build()){

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build();

                ResponseInputStream<?> objectInputStream = s3Client.getObject(getObjectRequest);
                byte[] imageBytes = objectInputStream.readAllBytes();

                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                images.add("data:image/jpeg;base64," + base64Image);

            } catch (Exception e) {
                throw new RuntimeException("Failed to get images from s3 bucket", e);
            }
        }

        logger.info("Worked inside getImagesByObjectKeysFromS3() images.size = " + images.size());

        return images;
    }
}
