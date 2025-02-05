package ua.orlov.springawsimage.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ua.orlov.springawsimage.service.DynamoDBService;
import ua.orlov.springawsimage.service.RekognitionService;
import ua.orlov.springawsimage.service.S3Service;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private RekognitionService rekognitionService;

    @Mock
    private DynamoDBService dynamoDBService;

    @InjectMocks
    private ImageServiceImpl imageService;

    @Test
    void uploadImageThenSuccess() {
        when(s3Service.loadImageToS3Bucket(any())).thenReturn("");
        when(rekognitionService.analyzeImageLabels(any())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> imageService.uploadImage(new MockMultipartFile("name", new byte[10])));

        verify(s3Service).loadImageToS3Bucket(any());
        verify(rekognitionService).analyzeImageLabels(any());
        verify(dynamoDBService).saveImageLabels(any(), anyList());
    }

    @Test
    void getImagesByFilterThenSuccess() {
        when(dynamoDBService.getImageUrlsByFilterTag(any())).thenReturn(new ArrayList<>());
        when(s3Service.getImagesByObjectKeysFromS3(any())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> imageService.getImagesByFilter("filter"));

        verify(dynamoDBService).getImageUrlsByFilterTag(any());
        verify(s3Service).getImagesByObjectKeysFromS3(any());
    }
}
