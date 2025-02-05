package ua.orlov.springawsimage.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import ua.orlov.springawsimage.service.DynamoDBService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DynamoDBServiceImplTest {

    private static final String DYNAMODB_TABLE_NAME = "test-table";

    @Mock
    private DynamoDbClient dynamoDbClient;

    private DynamoDBServiceImpl dynamoDBService;

    @Captor
    private ArgumentCaptor<PutItemRequest> putItemRequestCaptor;

    @Captor
    private ArgumentCaptor<ScanRequest> scanRequestCaptor;

    @BeforeEach
    void setUp() {
        dynamoDBService = new DynamoDBServiceImpl(dynamoDbClient, DYNAMODB_TABLE_NAME);
    }

    @Test
    void testSaveImageLabelsThenSuccess() {
        String s3Key = "test-image.jpg";
        List<String> labels = Arrays.asList("Label1", "Label2");

        when(dynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(PutItemResponse.builder().build());

        dynamoDBService.saveImageLabels(s3Key, labels);

        verify(dynamoDbClient).putItem(putItemRequestCaptor.capture());

        PutItemRequest request = putItemRequestCaptor.getValue();
        assertEquals(DYNAMODB_TABLE_NAME, request.tableName());
        assertTrue(request.item().containsKey("id"));
        assertEquals(s3Key, request.item().get("s3key").s());
        assertEquals(labels, request.item().get("labels").ss());
    }

    @Test
    void testSaveImageLabelsThenExceptionHandling() {
        String s3Key = "test-image.jpg";
        List<String> labels = Arrays.asList("Label1", "Label2");

        when(dynamoDbClient.putItem(any(PutItemRequest.class)))
                .thenThrow(new RuntimeException("DynamoDB error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            dynamoDBService.saveImageLabels(s3Key, labels);
        });

        assertEquals("DynamoDB error", exception.getMessage());
        verify(dynamoDbClient).putItem(any(PutItemRequest.class));
    }

    @Test
    void testGetImageUrlsByFilterTagThenSuccess() {
        String filterTag = "Label1";
        Map<String, AttributeValue> item1 = Map.of(
                "s3key", AttributeValue.builder().s("test-image1.jpg").build(),
                "labels", AttributeValue.builder().ss("Label1", "Label2").build()
        );
        Map<String, AttributeValue> item2 = Map.of(
                "s3key", AttributeValue.builder().s("test-image2.jpg").build(),
                "labels", AttributeValue.builder().ss("Label1").build()
        );

        ScanResponse scanResponse = ScanResponse.builder()
                .items(Arrays.asList(item1, item2))
                .build();

        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResponse);

        List<String> result = dynamoDBService.getImageUrlsByFilterTag(filterTag);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("test-image1.jpg"));
        assertTrue(result.contains("test-image2.jpg"));

        verify(dynamoDbClient).scan(scanRequestCaptor.capture());

        ScanRequest request = scanRequestCaptor.getValue();
        assertEquals(DYNAMODB_TABLE_NAME, request.tableName());
        assertEquals("contains(labels, :filterTag)", request.filterExpression());
    }

    @Test
    void testGetImageUrlsByFilterTagThenEmptyResult() {
        String filterTag = "NonExistentLabel";
        ScanResponse scanResponse = ScanResponse.builder()
                .items(Collections.emptyList())
                .build();

        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResponse);

        List<String> result = dynamoDBService.getImageUrlsByFilterTag(filterTag);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dynamoDbClient).scan(scanRequestCaptor.capture());

        ScanRequest request = scanRequestCaptor.getValue();
        assertEquals(DYNAMODB_TABLE_NAME, request.tableName());
        assertEquals("contains(labels, :filterTag)", request.filterExpression());
    }

    @Test
    void testGetImageUrlsByFilterTagThenExceptionHandling() {
        String filterTag = "Label1";
        when(dynamoDbClient.scan(any(ScanRequest.class)))
                .thenThrow(new RuntimeException("DynamoDB error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            dynamoDBService.getImageUrlsByFilterTag(filterTag);
        });

        assertEquals("DynamoDB error", exception.getMessage());
        verify(dynamoDbClient).scan(any(ScanRequest.class));
    }
}
