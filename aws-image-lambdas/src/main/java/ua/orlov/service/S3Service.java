package ua.orlov.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.orlov.dto.UploadImageRequest;

public class S3Service {

    //TODO Get from the properties file
    private static final String BUCKET_NAME = "orlov-image-bucket";

    public boolean loadImageToS3Bucket(UploadImageRequest request, LambdaLogger logger) {
        try (S3Client s3Client = S3Client.builder().build()) {

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(request.getFileName())
                    .contentType("image/jpeg")
                    .build();

            RequestBody body = RequestBody.fromBytes(request.getImageData());
            s3Client.putObject(putRequest, body);

            logger.log("Successfully uploaded: " + request.getFileName());
        } catch (Exception e) {
            logger.log("Upload failed: " + e.getMessage());
            return false;
        }

        return true;
    }

}
