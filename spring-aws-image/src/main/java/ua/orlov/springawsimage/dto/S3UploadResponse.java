package ua.orlov.springawsimage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class S3UploadResponse {
    private String key;
}
