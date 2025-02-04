<h1>Commands in order to put .jar file inside the s3 bucket.</h1>

# Run this command to compile the program
mvn clean package -DskipTests=true

# Run this command to create s3 using cloud formation
aws cloudformation deploy --template-file cloudformation/s3/lambdas-s3-bucket.yml --stack-name LambdaCodeBucketStack

# Run this command to put .jar file inside the bucket
aws s3 cp target/aws-image-lambdas-1.0-SNAPSHOT.jar s3://lambda-code-storage-bucket/lambda/aws-image-lambdas.jar

<h1>Commands in order to create a s3 bucket for saving photos.</h1>

# Run this command to create s3 using cloud formation
aws cloudformation deploy --template-file cloudformation/s3/images-s3-bucket.yml --stack-name ImageS3BucketStack
