package ua.orlov.springawsimage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    boolean uploadImage(MultipartFile image);

    List<String> getImagesByFilter(String filter);

}
