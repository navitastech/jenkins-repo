#!/bin/bash -e

export TERM=xterm

export REGION="us-east-1"
export APP_NAME="${STACK_TYPE}"
export TF_STATE_STORE="variq-terraform"
export TF_STATE_FOLDER="/${STACK_TYPE}"


cd $APP_NAME

terraform remote config \
        -backend=s3 \
        -backend-config="bucket=${TF_STATE_STORE}" \
        -backend-config="key=${TF_STATE_FOLDER}/${STACK_NAME}" \
        -backend-config="region=us-east-1" \
        -backend-config="encrypt=1"


terraform get -update

terraform remote config \
    -backend=s3 \
    -backend-config="bucket=${TF_STATE_STORE}" \
    -backend-config="key=${TF_STATE_FOLDER}/${STACK_NAME}" \
    -backend-config="region=us-east-1" \
    -backend-config="encrypt=1"

terraform apply

    if [[ $? -eq 0 ]]
    then
      status=SUCCESS
    else
      status=FAILURE
    fi

  #terraform remote push

  cat .terraform/terraform.tfstate

  echo "**************************** Stack Name = ${STACK_NAME} ****************************"

  if [[ "$status" != "SUCCESS" ]]
  then
    exit 1
  fi
