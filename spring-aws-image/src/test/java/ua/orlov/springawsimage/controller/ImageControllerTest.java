package ua.orlov.springawsimage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.orlov.springawsimage.service.ImageService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(imageController)
                .build();
    }

    @Test
    void uploadImageThenSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "mock image content".getBytes()
        );

        when(imageService.uploadImage(any())).thenReturn(true);

        MvcResult result = mockMvc.perform(multipart("/api/v1/uploadImage")
                        .file(file))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("File uploaded successfully", responseBody);

        verify(imageService).uploadImage(any());
    }

    @Test
    void uploadImageThenFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "mock image content".getBytes()
        );

        when(imageService.uploadImage(any())).thenReturn(false);

        MvcResult result = mockMvc.perform(multipart("/api/v1/uploadImage")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("Failed to upload photo", responseBody);
        verify(imageService).uploadImage(any());
    }

    @Test
    void getImagesByFilter() throws Exception {
        when(imageService.getImagesByFilter(any())).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(get("/api/v1/get-by-filter?filterWord=word"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("[]", responseBody);
        verify(imageService).getImagesByFilter(any());
    }

}
