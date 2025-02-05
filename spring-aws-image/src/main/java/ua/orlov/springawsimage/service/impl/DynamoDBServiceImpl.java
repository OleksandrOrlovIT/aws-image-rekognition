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

    private final DynamoDbClient dynamoDbClient;

    private final String dynamodbTableName;

    public DynamoDBServiceImpl(DynamoDbClient dynamoDbClient, @Value("${dynamodb.table.name}") String dynamodbTableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamodbTableName = dynamodbTableName;
    }

    @Override
    public void saveImageLabels(String s3Key, List<String> labels) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(dynamodbTableName)
                .item(Map.of(
                        "id", AttributeValue.builder().s(UUID.randomUUID().toString()).build(),
                        "s3key", AttributeValue.builder().s(s3Key).build(),
                        "labels", AttributeValue.builder().ss(labels).build()
                ))
                .build());

        logger.info("Image labels saved to DynamoDB for {}", s3Key);

    }

    @Override
    public List<String> getImageUrlsByFilterTag(String filterTag) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(dynamodbTableName)
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
    }
}
