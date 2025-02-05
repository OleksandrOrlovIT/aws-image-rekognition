package ua.orlov.springawsimage.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import ua.orlov.springawsimage.service.RekognitionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RekognitionServiceImpl implements RekognitionService {

    //TODO Get from the properties file
    private static final String BUCKET_NAME = "orlov-image-bucket";
    private static final Logger logger = LoggerFactory.getLogger(RekognitionService.class);

    @Override
    public List<String> analyzeImageLabels(String imageKey) {
        try {
            RekognitionClient rekognitionClient = RekognitionClient.builder()
                    .build();

            DetectLabelsRequest request = DetectLabelsRequest.builder()
                    .image(Image.builder()
                            .s3Object(S3Object.builder()
                                    .bucket(BUCKET_NAME)
                                    .name(imageKey)
                                    .build())
                            .build())
                    .maxLabels(30)
                    .minConfidence(50F)
                    .build();

            DetectLabelsResponse response = rekognitionClient.detectLabels(request);

            List<String> labels = response.labels().stream()
                    .map(Label::name)
                    .collect(Collectors.toList());

            logger.info("Received labels for imageKey: " + imageKey + " labels: " + labels);

            return labels;

        } catch (RekognitionException e) {
            logger.error("Rekognition failed: {}", e.getMessage());
            throw new RuntimeException("Failed to analyze image", e);
        }
    }
}
