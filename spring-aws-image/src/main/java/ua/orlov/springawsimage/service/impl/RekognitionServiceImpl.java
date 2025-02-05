package ua.orlov.springawsimage.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import ua.orlov.springawsimage.service.RekognitionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RekognitionServiceImpl implements RekognitionService {

    private static final Logger logger = LoggerFactory.getLogger(RekognitionService.class);

    private final RekognitionClient rekognitionClient;

    private final String bucketName;

    public RekognitionServiceImpl(RekognitionClient rekognitionClient, @Value("${s3.bucket.name}") String bucketName) {
        this.rekognitionClient = rekognitionClient;
        this.bucketName = bucketName;
    }

    @Override
    public List<String> analyzeImageLabels(String imageKey) {
        DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(Image.builder()
                        .s3Object(S3Object.builder()
                                .bucket(bucketName)
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

    }
}
