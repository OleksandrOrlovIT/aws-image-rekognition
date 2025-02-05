mvn clean package -DskipTests=true

docker build -t spring-aws-image .

docker save -o spring-aws-image.tar spring-aws-image

#Local development to test in docker
docker run -d --name spring-aws-image-container -p 8080:8080 --env-file .env -e AWS_REGION=eu-central-1 -e AWS_ACCESS_KEY_ID= -e AWS_SECRET_ACCESS_KEY= spring-aws-image

#At ec2 instance
docker run -d --name spring-aws-image-container -p 8080:8080 --env-file .env spring-aws-image

****************
.env should contain ->

DYNAMODB_TABLE_NAME=

S3_BUCKET_NAME=

FRONT_END_URL=
****************

aws s3 cp spring-aws-image.tar s3://orlovoleksandrs3/aws-image-tars/

***************
#cloud-config
packages:
- httpd
- docker

runcmd:
- sudo systemctl enable docker
- sudo systemctl start docker

# Install AWS CLI if not available
- sudo dnf install -y aws-cli

# Fetch files from S3 (ensure IAM role has correct permissions)
- aws s3 cp s3://orlovoleksandrs3/aws-image-tars/spring-aws-image.tar home/ec2-user/spring-aws-image.tar

# Load and run Docker container
- sudo docker load -i home/ec2-user/spring-aws-image.tar
