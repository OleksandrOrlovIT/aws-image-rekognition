package ua.orlov.springawsimage.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.ResponseInputStream;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    private static final String bucketName = "test-bucket";

    @Mock
    private S3Client s3Client;

    private S3ServiceImpl s3Service;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @BeforeEach
    void setUp() {
        s3Service = new S3ServiceImpl(s3Client, bucketName);
    }

    @Test
    void testLoadImageToS3BucketThenSuccess() throws IOException {
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test data".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());

        String result = s3Service.loadImageToS3Bucket(file);

        assertNotNull(result);
        verify(s3Client).putObject(putObjectRequestCaptor.capture(), any(RequestBody.class));
        PutObjectRequest request = putObjectRequestCaptor.getValue();
        assertEquals(bucketName, request.bucket());
    }

    @Test
    void testLoadImageToS3BucketThenFailWhenIOExceptionOccurs() {
        MultipartFile file = mock(MultipartFile.class);
        try {
            when(file.getBytes()).thenThrow(new IOException("Failed to convert image to bytes"));
        } catch (IOException e) {
            fail("Mock setup failed");
        }

        Exception exception = assertThrows(RuntimeException.class, () -> s3Service.loadImageToS3Bucket(file));
        assertEquals("Failed to convert image to bytes", exception.getMessage());
    }

    @Test
    void testGetImagesByObjectKeysFromS3ThenSuccess() throws IOException {
        String objectKey = "test-object-key";
        List<String> keys = Collections.singletonList(objectKey);
        byte[] imageBytes = "image data".getBytes();
        ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);
        when(responseStream.readAllBytes()).thenReturn(imageBytes);

        List<String> result = s3Service.getImagesByObjectKeysFromS3(keys);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).startsWith("data:image/jpeg;base64,"));
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    void testGetImagesByObjectKeysFromS3ThenFailWhenIOExceptionOccurs() throws IOException {
        String objectKey = "test-object-key";
        List<String> keys = Collections.singletonList(objectKey);
        ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);
        when(responseStream.readAllBytes()).thenThrow(new IOException("Failed to read object"));

        Exception exception = assertThrows(RuntimeException.class, () -> s3Service.getImagesByObjectKeysFromS3(keys));
        assertEquals("java.io.IOException: Failed to read object", exception.getMessage());
    }
}
