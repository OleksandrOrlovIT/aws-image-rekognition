package ua.orlov.springawsimage.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.orlov.springawsimage.service.DynamoDBService;
import ua.orlov.springawsimage.service.ImageService;
import ua.orlov.springawsimage.service.RekognitionService;
import ua.orlov.springawsimage.service.S3Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private final S3Service s3Service;
    private final RekognitionService rekognitionService;
    private final DynamoDBService dynamoDBService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean uploadImage(MultipartFile image) {
        String objectKey = s3Service.loadImageToS3Bucket(image);

        List<String> s3ObjectLabels = rekognitionService.analyzeImageLabels(objectKey);

        dynamoDBService.saveImageLabels(objectKey, s3ObjectLabels);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> getImagesByFilter(String filter) {
        List<String> s3ObjectKeys = dynamoDBService.getImageUrlsByFilterTag(filter);

        return s3Service.getImagesByObjectKeysFromS3(s3ObjectKeys);
    }
}
