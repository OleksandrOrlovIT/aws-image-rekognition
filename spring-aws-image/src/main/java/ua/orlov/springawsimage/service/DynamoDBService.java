package ua.orlov.springawsimage.service;

import java.util.List;

public interface DynamoDBService {

    void saveImageLabels(String imageUrl, List<String> labels);

    List<String> getImageUrlsByFilterTag(String filterTag);
}
