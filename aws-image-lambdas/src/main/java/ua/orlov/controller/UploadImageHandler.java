package ua.orlov.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import ua.orlov.dto.DefaultResponse;
import ua.orlov.dto.UploadImageRequest;
import ua.orlov.service.S3Service;

public class UploadImageHandler implements RequestHandler<UploadImageRequest, Object> {

    private final S3Service s3Service;

    public UploadImageHandler() {
        s3Service = new S3Service();
    }

    @Override
    public Object handleRequest(UploadImageRequest request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling request for file: " + request.getFileName());

        if(!s3Service.loadImageToS3Bucket(request, logger)) {
            return new DefaultResponse("Failed to upload photo", 400);
        }

        return new DefaultResponse("Everything worked", 200);
    }
}
