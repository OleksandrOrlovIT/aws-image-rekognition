package ua.orlov.springawsimage.service;

import java.util.List;

public interface RekognitionService {

    List<String> analyzeImageLabels(String imageKey);

}
