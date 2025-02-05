<h1>Commands in order to create s3 bucket for storing images.</h1>

# Run this command to compile the program
mvn clean package -DskipTests=true

<h1>Commands in order to create a s3 bucket for saving photos.</h1>

# Run this command to create s3 using cloud formation
aws cloudformation deploy --template-file cloudformation/s3/images-s3-bucket.yml --stack-name ImageS3BucketStack
