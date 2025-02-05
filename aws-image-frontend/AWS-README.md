docker build -t aws-image-frontend .

docker save -o aws-image-frontend.tar aws-image-frontend

# Run the container with AWS environment variables
docker run -d --name aws-image-frontend-container -p 3000:80 -e REACT_APP_API_URL="" aws-image-frontend

aws s3 cp aws-image-frontend.tar s3://orlovoleksandrs3/aws-image-tars/

****************
.env should contain ->

REACT_APP_API_URL=
****************

****************
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
- aws s3 cp s3://orlovoleksandrs3/aws-image-tars/aws-image-frontend.tar home/ec2-user/aws-image-frontend.tar

# Load and run Docker container
- sudo docker load -i home/ec2-user/aws-image-frontend.tar
