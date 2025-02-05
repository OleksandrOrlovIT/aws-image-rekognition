package ua.orlov.springawsimage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface S3Service {

    //Returns s3 object key
    String loadImageToS3Bucket(MultipartFile img);

    List<String> getImagesByObjectKeysFromS3(List<String> keys);
}
