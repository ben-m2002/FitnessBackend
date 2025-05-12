# 1) Run your Spring Boot task manually and capture its ARN
export CLUSTER=bylt-cluster
export TASK_DEF=bylt-api:10      # adjust if needed

TASK_JSON=$(aws ecs run-task \
  --cluster "$CLUSTER" \
  --launch-type FARGATE \
  --task-definition "$TASK_DEF" \
  --network-configuration 'awsvpcConfiguration={subnets=["subnet-08cbb4aaf92f0d5b9","subnet-0a253890a77c40e11"],securityGroups=["sg-0643ddec2b54e1916"],assignPublicIp=ENABLED}' \
  --output json)

# 2) Extract the task ARN and ID
export TASK_ARN=$(echo "$TASK_JSON" | jq -r '.tasks[0].taskArn')
export TASK_ID=${TASK_ARN##*/}
echo "Launched task: $TASK_ARN"

# 3) Wait for it to spin up & emit logs
sleep 20

# 4) Find the log stream named for this task ID
export STREAM_NAME=$(aws logs describe-log-streams \
  --log-group-name /ecs/bylt \
  --order-by LastEventTime \
  --descending \
  --limit 5 \
  --query "logStreams[?ends_with(logStreamName, '$TASK_ID')].logStreamName" \
  --output text)

echo "Found stream: $STREAM_NAME"

# 5) Tail its logs
aws logs get-log-events \
  --log-group-name /ecs/bylt \
  --log-stream-name "$STREAM_NAME" \
  --limit 50 \
  --query 'events[*].message' \
  --output text