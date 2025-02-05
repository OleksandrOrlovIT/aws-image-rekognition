package ua.orlov.springawsimage.service.impl;

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

import java.io.IOException;
import java.util.*;

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    private final S3Client s3Client;

    private final String bucketName;

    public S3ServiceImpl(S3Client s3Client, @Value("${s3.bucket.name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String loadImageToS3Bucket(MultipartFile img) {
        String uniqueKey = UUID.randomUUID() + "-" + img.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .contentType(img.getContentType())
                .build();

        RequestBody body;
        try {
            body = RequestBody.fromBytes(img.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to bytes");
        }

        s3Client.putObject(putRequest, body);

        String imageUrl = "https://" + bucketName + ".s3.amazonaws.com/" + uniqueKey;
        logger.info("Successfully uploaded: {}", imageUrl);

        return uniqueKey;
    }

    @Override
    public List<String> getImagesByObjectKeysFromS3(List<String> keys) {
        List<String> images = new ArrayList<>();

        for (String key : keys) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseInputStream<?> objectInputStream = s3Client.getObject(getObjectRequest);

            byte[] imageBytes;

            try {
                imageBytes = objectInputStream.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            images.add("data:image/jpeg;base64," + base64Image);
        }

        logger.info("Worked inside getImagesByObjectKeysFromS3() images.size = " + images.size());

        return images;
    }
}
