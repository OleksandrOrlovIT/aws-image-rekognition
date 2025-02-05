package ua.orlov.springawsimage.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import ua.orlov.springawsimage.service.RekognitionService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RekognitionServiceImplTest {

    private static final String BUCKET_NAME = "test-bucket";

    @Mock
    private RekognitionClient rekognitionClient;

    private RekognitionServiceImpl rekognitionService;

    @Captor
    private ArgumentCaptor<DetectLabelsRequest> detectLabelsRequestCaptor;

    @BeforeEach
    void setUp() {
        rekognitionService = new RekognitionServiceImpl(rekognitionClient, BUCKET_NAME);
    }

    @Test
    void testAnalyzeImageLabelsThenSuccess() {
        String imageKey = "test-image.jpg";
        List<Label> mockLabels = Arrays.asList(
                Label.builder().name("Label1").build(),
                Label.builder().name("Label2").build()
        );
        
        DetectLabelsResponse mockResponse = DetectLabelsResponse.builder()
                .labels(mockLabels)
                .build();

        when(rekognitionClient.detectLabels(any(DetectLabelsRequest.class))).thenReturn(mockResponse);

        List<String> labels = rekognitionService.analyzeImageLabels(imageKey);

        assertNotNull(labels);
        assertEquals(2, labels.size());
        assertTrue(labels.contains("Label1"));
        assertTrue(labels.contains("Label2"));
        verify(rekognitionClient).detectLabels(detectLabelsRequestCaptor.capture());

        DetectLabelsRequest request = detectLabelsRequestCaptor.getValue();
        assertEquals(BUCKET_NAME, request.image().s3Object().bucket());
        assertEquals(imageKey, request.image().s3Object().name());
    }

    @Test
    void testAnalyzeImageLabelsThenEmptyLabels() {
        String imageKey = "test-image.jpg";
        List<Label> mockLabels = Collections.emptyList();
        
        DetectLabelsResponse mockResponse = DetectLabelsResponse.builder()
                .labels(mockLabels)
                .build();

        when(rekognitionClient.detectLabels(any(DetectLabelsRequest.class))).thenReturn(mockResponse);

        List<String> labels = rekognitionService.analyzeImageLabels(imageKey);

        assertNotNull(labels);
        assertTrue(labels.isEmpty());
        verify(rekognitionClient).detectLabels(detectLabelsRequestCaptor.capture());

        DetectLabelsRequest request = detectLabelsRequestCaptor.getValue();
        assertEquals(BUCKET_NAME, request.image().s3Object().bucket());
        assertEquals(imageKey, request.image().s3Object().name());
    }

    @Test
    void testAnalyzeImageLabelsThenExceptionHandling() {
        String imageKey = "test-image.jpg";
        
        when(rekognitionClient.detectLabels(any(DetectLabelsRequest.class)))
                .thenThrow(new RuntimeException("AWS Rekognition service failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rekognitionService.analyzeImageLabels(imageKey);
        });
        
        assertEquals("AWS Rekognition service failure", exception.getMessage());
        verify(rekognitionClient).detectLabels(any(DetectLabelsRequest.class));
    }
}
