<h1>Commands in order to create a lambda function.(Do only after creating an s3 bucket)</h1>

# Command to delete old lambda
aws cloudformation delete-stack --stack-name LambdaFunctionStack

# Command to create a lambda
aws cloudformation deploy --template-file cloudformation/lambda/upload-image-lambda.yml --stack-name LambdaFunctionStack --capabilities CAPABILITY_NAMED_IAM

# In order to invoke lambda I recommend to use aws console in the browser, or you can run it using terminal with
# next command (You need to put a json file inside resources or to pass it manually. Output.json will be generated inside the project)
aws lambda invoke --cli-binary-format raw-in-base64-out --function-name ImageUploadFunction --payload file://src/main/resources/imageLoading.json output.json

<p>Example of a correct json 
{
 "fileName": "defaultimg.jpg",
 "imageData": [],
}
</p>
