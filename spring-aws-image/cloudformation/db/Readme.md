aws cloudformation validate-template --template-body file://cloudformation/db/dynamodb-image-labels.yml

aws cloudformation create-stack --stack-name ImageLabelsStack --template-body file://cloudformation/db/dynamodb-image-labels.yml

aws cloudformation describe-stacks --stack-name ImageLabelsStack --query "Stacks[0].StackStatus"

aws dynamodb describe-table --table-name ImageLabels
