#!/bin/bash -e

export TERM=xterm

export REGION="us-east-1"
export APP_NAME="${STACK_TYPE}"
export TF_STATE_STORE="variq-terraform"
export TF_STATE_FOLDER="/${STACK_TYPE}"


cd $APP_NAME

STATE_FILE="s3://${TF_STATE_STORE}${TF_STATE_FOLDER}/${STACK_NAME}"

count=$(aws s3 ls ${STATE_FILE} | wc -l)
  if [ $count -gt 0 ]
  then
    terraform remote config \
        -backend=s3 \
        -backend-config="bucket=${TF_STATE_STORE}" \
        -backend-config="key=${TF_STATE_FOLDER}/${STACK_NAME}" \
        -backend-config="region=us-east-1" \
        -backend-config="encrypt=1"
  fi


terraform get -update

terraform plan
