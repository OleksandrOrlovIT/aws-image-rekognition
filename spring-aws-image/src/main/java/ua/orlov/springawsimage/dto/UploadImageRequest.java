package ua.orlov.springawsimage.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadImageRequest {

    private MultipartFile image;

}
