package ua.orlov.springawsimage.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import ua.orlov.springawsimage.service.DynamoDBService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DynamoDBServiceImpl implements DynamoDBService {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBService.class);

    @Value("${dynamodb.table.name}")
    private String DYNAMODB_TABLE_NAME;

    @Override
    public void saveImageLabels(String s3Key, List<String> labels) {
        try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build()){
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(DYNAMODB_TABLE_NAME)
                    .item(Map.of(
                            "id", AttributeValue.builder().s(UUID.randomUUID().toString()).build(),
                            "s3key", AttributeValue.builder().s(s3Key).build(),
                            "labels", AttributeValue.builder().ss(labels).build()
                    ))
                    .build());

            logger.info("Image labels saved to DynamoDB for {}", s3Key);

        } catch (DynamoDbException e) {
            logger.error("Failed to save labels to DynamoDB: {}", e.getMessage());
            throw new RuntimeException("Error saving image labels", e);
        }
    }

    @Override
    public List<String> getImageUrlsByFilterTag(String filterTag) {
        try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build()){
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(DYNAMODB_TABLE_NAME)
                    .filterExpression("contains(labels, :filterTag)")
                    .expressionAttributeValues(Map.of(
                            ":filterTag", AttributeValue.builder().s(filterTag).build()
                    ))
                    .build();

            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

            List<String> s3Objects = scanResponse.items().stream()
                    .map(item -> item.get("s3key").s())
                    .toList();

            logger.info("S3 objects retrieved by filter {} , {}", filterTag, s3Objects);

            return s3Objects;
        } catch (DynamoDbException e) {
            logger.error("Failed to query DynamoDB: {}", e.getMessage());
            throw new RuntimeException("Error retrieving image URLs", e);
        }
    }
}
